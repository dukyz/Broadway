package actor.crawler

import akka.actor.{Actor, ActorRef}
import common.tool.{ActorUtil, DownloadUtil}

/**
  * @todo to manage actors organized by crawler module
  *
  * @author dukyz
  */

class CrawlerManager extends Actor with ActorUtil with DownloadUtil{
    
    var wordDispatcher:ActorRef = null
    
    override def preStart = {
        wordDispatcher = generateSingletonActor[WordDispatcher]
        registerStuff("crawlerManager",context.self)
        .registerStuff("wordDispatcher",wordDispatcher)
    }
    
    override def receive: Receive = {
        case _ =>
    }
    
}