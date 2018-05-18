package actor.proxy

import akka.actor.{Actor, ActorRef}
import common.tool.ActorUtil

/**
  * @todo to manage actors organized by proxy module
  *
  * @author dukyz
  */
class ProxyManager extends Actor with ActorUtil{
    
    var proxyGraber:ActorRef = null
    var proxyFlusher:ActorRef = null
    var proxySeller:ActorRef = null
    var proxyChecker:ActorRef = null
    var proxySaver:ActorRef = null
    
    override def preStart = {
        proxyGraber = generateSingletonActor[ProxyGraber]
        proxyFlusher = generateSingletonActor[ProxyFlusher]
        proxySeller = generateSingletonActor[ProxySeller]
        proxyChecker = generateShardingActor[ProxyChecker](classConfig.getInt("proxyCheckerCount"))
        proxySaver = generateShardingActor[ProxySaver](classConfig.getInt("proxySaverCount"))
    
        registerStuff("proxyManager",context.self)
        .registerStuff("proxyFlusher",proxyFlusher)
        .registerStuff("proxyGraber",proxyGraber)
        .registerStuff("proxySeller",proxySeller)
        .registerStuff("proxyChecker",proxyChecker)
        .registerStuff("proxySaver",proxySaver)
    }
    
    override def receive: Receive = {
        case _ =>
    }
    
}
