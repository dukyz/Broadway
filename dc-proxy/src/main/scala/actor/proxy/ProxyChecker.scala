package actor.proxy

import actor.proxy.ProxyChecker.Check
import akka.actor.Actor
import common.tool.ActorUtil

object ProxyChecker {
    case class Check(proxy:String)
}

class ProxyChecker extends Actor with ActorUtil {
    
    override def receive = {
        case Check(proxy) => check(proxy)
        case _ =>
    }
    
    def check(proxy:String) = {
        var passed = false
        
        
        //测试代理有效性代码
        
        
//        if (passed)
//            actorRegistration.findStuff[ProxySaver].get ! ProxySaver.SaveChecked(proxy)
    }
}
