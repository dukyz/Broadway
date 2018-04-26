package actor.crawler.searchengine.baidu

import java.net.URLEncoder
import java.nio.charset.Charset

import akka.actor.Actor
import common.tool.ActorUtil
import io.netty.util.CharsetUtil
import org.htmlcleaner.{CleanerProperties, HtmlCleaner, SimpleHtmlSerializer}

import scala.util.{Failure, Success}

/**
  * @todo Search engine for baidu.
  *
  * @author dukyz
  */

class Searcher extends Actor with ActorUtil{
    
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
        //parse the url
//        networkUtil.download(url,searchEngineCharset).onComplete({
//            case Success(html) => {
//                println(html)
//                htmlCleaner.clean(html).evaluateXPath(url_Xpath)
//                    .map(_.asInstanceOf[String])
//                    .filter(match_regexp_afterXpath.findFirstIn(_).isDefined)
//                    .filterNot(drop_regexp_afterXpath.findFirstIn(_).isEmpty)
//                    .toSet[String]
//                    .foreach( link => {
//                        networkUtil.getHeaders(link).onComplete({
//                            case Success(headers) => {
//                                val redirectLocation = headers.filter(_.name().equals("Location")).head.value()
//                                networkUtil.download(redirectLocation).onComplete({
//                                    case Success(html) => {
//
//                                        val cp = new CleanerProperties()
//                                        val cleaned = new SimpleHtmlSerializer(cp)
//                                            .getAsString(htmlCleaner.clean(html))
//                                        val newCleaned = new String(cleaned.getBytes(),"")
//
//
////                                        cassandraSession.execute(saveUrl.bind(redirectLocation,cleaned))
//                                            println(newCleaned)
//
//                                    }
//                                    case Failure(f) =>
//                                })
//                            }
//                            case Failure(f) =>
//                        })
//                    })
//            }
//            case Failure(f) =>
//        })
    }
}



