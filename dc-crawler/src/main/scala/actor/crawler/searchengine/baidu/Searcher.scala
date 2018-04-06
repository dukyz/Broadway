package actor.crawler.searchengine.baidu

import actor.crawler.UrlCollector
import akka.actor.{Actor, ActorRef}
import common.tool.ActorUtil
import org.htmlcleaner.HtmlCleaner

import scala.util.{Failure, Success}

class Searcher extends Actor with ActorUtil{
    
    val url_pattern = classConfig.getString("url_pattern")
    val url_Xpath = classConfig.getString("url_Xpath")
    val match_regexp_afterXpath = classConfig.getString("match_regexp_afterXpath").r
    val drop_regexp_afterXpath = classConfig.getString("drop_regexp_afterXpath").r
    var urlCollector:ActorRef = null
    
    override def preStart(): Unit = {
        urlCollector = actorRegistration.findStuff[UrlCollector].get
    }
    
    def receive = {
        case keyword:String => search(keyword)
        case _ =>
    }
    
    def search(keyword:String):Unit = {
        
        val url = url_pattern.replace("{{KEYWORD}}","keyword")
        
        networkUtil.download(url).onComplete({
            case Success(html) => {
                new HtmlCleaner().clean(html).evaluateXPath(url_Xpath)
                    .map(_.asInstanceOf[String])
                    .filter( url => match_regexp_afterXpath.findFirstIn(url).isDefined)
                    .filterNot( url => drop_regexp_afterXpath.findFirstIn(url).isEmpty)
                    .foreach(urlCollector !)
            }
            case Failure(f) =>
        })
        
    }
}


