package actor.crawler

import akka.actor.{Actor, ActorRef}
import common.tool.ActorUtil

/**
  * @todo to manage actors organized by crawler module
  *
  * @author dukyz
  */

class CrawlerManager extends Actor with ActorUtil{
    
    var wordDispatcher:ActorRef = null
    
    override def preStart = {
        wordDispatcher = generateSingletonActor[WordDispatcher]
        actorRegistration.registerStuff("crawlerManager",context.self)
        .registerStuff("wordDispatcher",wordDispatcher)
    }
    
    override def receive: Receive = {
        case _ =>
    }
    
}