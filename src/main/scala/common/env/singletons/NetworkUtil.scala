package common.env.singletons

import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}

import scala.concurrent.duration.FiniteDuration

private[env] object NetworkUtil {
    import Config.baseConfig
    
    def checkProxy(proxyHost:String,proxyPort:Int,targetUri:String)(implicit actorSystem:ActorSystem) = {
        val httpsProxyTransport = ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxyHost, proxyPort))
        val settings = ConnectionPoolSettings(actorSystem)
                .withMaxRetries(baseConfig.getInt("default.common.env.singletons.ProxyUtil.max-retries"))
                .withConnectionSettings(
                    ClientConnectionSettings(actorSystem)
                        .withTransport(httpsProxyTransport)
                        .withConnectingTimeout(FiniteDuration(
                            baseConfig.getInt("default.common.env.singletons.ProxyUtil.connection-timeout"),
                            TimeUnit.SECONDS)
                        )
                )
            
        Http().singleRequest(HttpRequest(uri = targetUri), settings = settings)
        
    }
   
    
    
}
