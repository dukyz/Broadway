package actor.proxy

import akka.actor.Actor
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import common.model.ProxyAgent
import common.setting.ShardingDefault
import common.tool.{ActorUtil, DownloadUtil}

import scala.util.Try

object ProxyChecker {
    case class Check(ts:Long,order_id:Int,proxy:String)
    val checkUri = classConfig.getString("checkUri")
}

class ProxyChecker extends Actor with ActorUtil with DownloadUtil{
    import ProxyChecker._
    
    override def receive = {
        case Check(ts,order_id,proxy) => check(ts,order_id,proxy)
        case _ =>
    }
    
    def check(ts:Long,order_id:Int,proxy:String) = {
        
        val proxyAgent = ProxyAgent(
            proxy.split(":")(0),
            proxy.split(":")(1).toInt
        )
        
        checkProxy(proxyAgent,checkUri).foreach({
            case resp @ HttpResponse(StatusCodes.OK, _, _, _) => {
                resp.discardEntityBytes()
                findStuff[ProxySaver].get !
                    ShardingDefault.EntityEnvelope(
                        ts.toString,
                        ProxySaver.saveCheckedIntoPool(ts,order_id,proxy)
                    )
            }
            case _ =>
        })
    
//        networkUtil.checkProxy(proxyHost,proxyPort,checkUri).foreach(
//            resp => {
//                resp.discardEntityBytes()
//                actorRegistration.findStuff[ProxySaver].get !
//                    ShardingDefault.EntityEnvelope(
//                        ts.toString,
//                        ProxySaver.saveCheckedIntoPool(ts,order_id,proxy)
//                    )
//            }
//
//        )
    }
}
