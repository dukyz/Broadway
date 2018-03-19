package actor.proxy

import actor.proxy.ProxySeller.Get
import akka.actor.Actor
import common.tool.ActorUtil
import scala.collection.JavaConversions.collectionAsScalaIterable

object ProxySeller   {
    case class Get(n:Int)
}

class ProxySeller extends Actor with ActorUtil{
    
    override def receive = {
        case Get(n) => cassandraSession.execute(s"select proxy from proxy.pool where limit $n")
            .all.map(row => row.getString("proxy"))
        case _ =>
    }
}
