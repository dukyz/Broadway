package actor.proxy

import akka.actor.{Actor, ActorRef}
import common.util.ActorUtil

class ProxyManager extends Actor with ActorUtil{
    
    var proxyGraber:ActorRef = null
    var proxyFlusher:ActorRef = null
    var proxySeller:ActorRef = null
    
    override def preStart = {
        proxyFlusher = generateNormalActor[ProxyFlusher]
        proxyGraber = generateSingletonActor[ProxyGraber]
        proxySeller = generateShardingActor[ProxySeller](3)
    }
    
    override def receive: Receive = {
        case _ =>
    }
    
}
