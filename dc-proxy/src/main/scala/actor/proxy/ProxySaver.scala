package actor.proxy

import actor.proxy.ProxySaver.{SaveChecked, SaveNew}
import akka.actor.Actor
import common.tool.ActorUtil

object ProxySaver {
    case class SaveNew(url:String)
    case class SaveChecked(url:String)
}

class ProxySaver extends Actor with ActorUtil {
    
    override def receive = {
        case SaveNew(url) =>
        case SaveChecked(url) =>
        case _ =>
    }
}
