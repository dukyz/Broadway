package actor.crawler.searchengine.baidu

import java.net.URLEncoder
import scala.concurrent.duration._
import akka.actor.Actor
import common.tool.ActorUtil
import org.htmlcleaner.HtmlCleaner

import scala.util.{Failure, Success}

/**
  * @todo Search engine for baidu.
  *
  * @author dukyz
  */

class Searcher extends Actor with ActorUtil{
    
    // search engine url
    private val url_pattern = classConfig.getString("url_pattern")
    // xpath which extract urls from the raw html
    private val url_Xpath = classConfig.getString("url_Xpath")
    // regexp that match urls which need to keep
    private val match_regexp_afterXpath = classConfig.getString("match_regexp_afterXpath").r
    // regexp that match urls which need to drop
    private val drop_regexp_afterXpath = classConfig.getString("drop_regexp_afterXpath").r
    
    private val saveUrl = cassandraSession.prepare("insert into crawler.url(url,html) values(?,?)")
    
    private val htmlCleaner:HtmlCleaner = new HtmlCleaner()
    
    override def preStart(): Unit = {
    
    }
    
    def receive = {
        case word:String => search(word)
        case _ =>
    }
    
    def search(word:String):Unit = {
        
        val url = url_pattern.replace("{{KEYWORD}}",URLEncoder.encode(word,"utf8"))
        
        //download the word
        //fetch and uniq urls in raw html
        //parse the url
        networkUtil.download(url).onComplete({
            case Success(html) => {
                println(html)
                htmlCleaner.clean(html).evaluateXPath(url_Xpath)
                    .map(_.asInstanceOf[String])
                    .filter(match_regexp_afterXpath.findFirstIn(_).isDefined)
                    .filterNot(drop_regexp_afterXpath.findFirstIn(_).isEmpty)
                    .toSet[String]
                    .foreach( link => {
                        networkUtil.downloadAll(link).onComplete({
                            case Success(resp) => {
                                val location = resp.headers.filter(_.name().equals("Location")).head.value()
                                resp.entity.toStrict(networkUtil.connectionTimeout seconds).map(_.data.utf8String).onComplete({
                                    case Success(html) => {
                                        cassandraSession.execute(saveUrl.bind(location,html))
                                        println(link)
                                        println(location)
                                    }
                                    case Failure(f) =>
                                })
                            }
                            case Failure(f) =>
                        })
                    }
                    )
            }
            case Failure(f) =>
        })
    }
}



