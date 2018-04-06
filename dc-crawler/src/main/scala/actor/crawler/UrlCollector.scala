package actor.crawler

import akka.actor.Actor
import common.tool.ActorUtil


object UrlCollector {
    case class URL(url:String)
}
class UrlCollector extends Actor with ActorUtil{
    override def receive: Receive = ???
}

