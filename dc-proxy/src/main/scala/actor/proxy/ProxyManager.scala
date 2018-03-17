package actor.proxy

import akka.actor.{Actor, ActorRef}
import common.tool.ActorUtil

class ProxyManager extends Actor with ActorUtil{
    
    var proxyGraber:ActorRef = null
    var proxyFlusher:ActorRef = null
    var proxySeller:ActorRef = null
    var proxyChecker:ActorRef = null
    var proxySaver:ActorRef = null
    
    override def preStart = {
        proxyFlusher = generateSingletonActor[ProxyFlusher]
        proxyGraber = generateSingletonActor[ProxyGraber]
        proxySeller = generateShardingActor[ProxySeller](classConfig.getInt("proxySellerCount"))
        proxyChecker = generateShardingActor[ProxyChecker](classConfig.getInt("proxyCheckerCount"))
        proxySaver = generateShardingActor[ProxySaver](classConfig.getInt("proxySaverCount"))
        
        actorRegistration.registerStuff("proxyFlusher",proxyFlusher)
        actorRegistration.registerStuff("proxyGraber",proxyGraber)
        actorRegistration.registerStuff("proxySeller",proxySeller)
        actorRegistration.registerStuff("proxyChecker",proxyChecker)
        actorRegistration.registerStuff("proxySaver",proxySaver)
    }
    
    override def receive: Receive = {
        case _ =>
    }
    
}
