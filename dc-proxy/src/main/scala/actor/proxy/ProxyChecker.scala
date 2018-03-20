package actor.proxy

import actor.proxy.ProxyChecker.Check
import akka.actor.Actor
import common.setting.ShardingDefault
import common.tool.ActorUtil

object ProxyChecker {
    case class Check(ts:Long,order_id:Int,proxy:String)
}

class ProxyChecker extends Actor with ActorUtil {
    
    override def receive = {
        case Check(ts,order_id,proxy) => check(ts,order_id,proxy)
        case _ =>
    }
    
    def check(ts:Long,order_id:Int,proxy:String) = {
        var passed = false
        //测试代理有效性代码
        passed = true
        
        if (passed)
            actorRegistration.findStuff[ProxySaver].get !
                ShardingDefault.EntityEnvelope(
                    ts.toString,
                    ProxySaver.saveCheckedIntoPool(ts,order_id,proxy)
                )
    }
}
