package actor.crawler.searchengine.baidu

import java.net.URLEncoder

import akka.actor.Actor
import common.tool.{ActorUtil, DownloadUtil}

import scala.util.{Failure, Success}

/**
  * @todo Search engine for baidu.
  *
  * @author dukyz
  */

class Searcher extends Actor with ActorUtil with DownloadUtil{
    
    // search engine pattern url
    private val searchEnginePatternUrl = classConfig.getString("searchEnginePatternUrl")
    // search engine page charset
    private val searchEngineCharset = classConfig.getString("searchEngineCharset")
    // xpath which extract links from the result html
    private val link_Xpath = classConfig.getString("link_Xpath")
    // regexp that match urls which need to keep
    private val match_regexp_afterXpath = classConfig.getString("match_regexp_afterXpath").r
    // regexp that match urls which need to drop
    private val drop_regexp_afterXpath = classConfig.getString("drop_regexp_afterXpath").r
    
    private val saveUrl = cassandraSession.prepare("insert into crawler.url(url,html) values(?,?)")
    
    override def preStart(): Unit = {
    
    }
    
    def receive = {
        case word:String => search(word)
        case _ =>
    }
    
    def search(word:String):Unit = {
        
        val url = searchEnginePatternUrl.replace("{{KEYWORD}}",URLEncoder.encode(word,"utf8"))
        
        //download the word
        //fetch and uniq urls in raw html
        //parse the url into standard form and utf-8 charset
        downloadHtmlString(url,searchEngineCharset).onComplete({
            case Success(html) => {
                htmlCleaner.clean(html).evaluateXPath(link_Xpath)
                    .map(_.asInstanceOf[String])
                    .filter(match_regexp_afterXpath.findFirstIn(_).isDefined)
                    .filterNot(drop_regexp_afterXpath.findFirstIn(_).isEmpty)
                    .toSet[String]
                    .foreach( link => {
                        downloadWithRelocate(link).onComplete({
                            case Success(data) => {
                                val urlTarget = data._1.split("#").head
                                val cleanedHtml = rectifyHtml(data._2)
                                cassandraSession.execute(saveUrl.bind(urlTarget,cleanedHtml))
                            }
                            case Failure(f) =>
                        })
                    })
            }
            case Failure(f) =>
        })
    }
}



