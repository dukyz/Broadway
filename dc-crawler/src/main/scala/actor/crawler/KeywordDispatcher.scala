package actor.crawler

import akka.actor.Actor
import common.tool.ActorUtil

object KeywordDispatcher {
    case class Keyword(name:String)
}

class KeywordDispatcher extends Actor with ActorUtil{
    
    import KeywordDispatcher._
    
    override def preStart(): Unit = {
    
    }
    
    override def receive: Receive = {
        
        case Keyword(name) => {
            check(name)
        }
        
        case _ =>
    }
    
    
    
    def check(keyword:String): Unit = {
    
    }
}

