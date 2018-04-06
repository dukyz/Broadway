package common.env.singletons
import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}
import akka.stream.Materializer

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

private[env] object NetworkUtil {
    import Config.baseConfig
    
    private val maxRetries = baseConfig.getInt("default.common.env.singletons.NetworkUtil.max-retries")
    private val connectionTimeout = baseConfig.getInt("default.common.env.singletons.NetworkUtil.connection-timeout")
    
    def checkProxy(proxyHost:String,proxyPort:Int,targetUri:String)(implicit actorSystem:ActorSystem) = {
        val httpsProxyTransport = ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxyHost, proxyPort))
        val settings = ConnectionPoolSettings(actorSystem)
                .withMaxRetries(maxRetries)
                .withConnectionSettings(
                    ClientConnectionSettings(actorSystem)
                        .withTransport(httpsProxyTransport)
                        .withConnectingTimeout(connectionTimeout.seconds)
                )
            
        Http().singleRequest(HttpRequest(uri = targetUri), settings = settings)
        
    }
   
    def download(targetUri:String,proxyHost:String=null,proxyPort:Int=8080)
                (implicit actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer) = {

        val settings = ConnectionPoolSettings(actorSystem)
            .withMaxRetries(maxRetries)
            .withConnectionSettings(
                ClientConnectionSettings(actorSystem)
                    .withConnectingTimeout(connectionTimeout.seconds)
            )

        if (proxyHost != null){
            settings.withTransport(
                ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxyHost, proxyPort))
            )
        }

        Http().singleRequest(HttpRequest(uri = targetUri), settings = settings).flatMap( resp =>
            resp.entity.toStrict(connectionTimeout.seconds).map(s=>s.data.utf8String)
        )

        //
    }
    
    
}
