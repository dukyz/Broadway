package common.env.singletons

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}
import akka.stream.Materializer
import akka.util.ByteString
import common.env.singletons.Config.baseConfig
import common.model.ProxyAgent
import org.htmlcleaner._

import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

private[env] object NetworkUtil {
    
    
    private val maxRetries = baseConfig.getInt("default.common.env.singletons.NetworkUtil.max-retries")
    val connectionTimeout = baseConfig.getInt("default.common.env.singletons.NetworkUtil.connection-timeout")
    val htmlCleaner = new HtmlCleaner()
    val serializer = new SimpleHtmlSerializer(htmlCleaner.getProperties())
    
    def checkProxy(proxy:ProxyAgent,targetUri:String)(implicit actorSystem:ActorSystem) = {
        val httpsProxyTransport = ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxy.host, proxy.port))
        val settings = ConnectionPoolSettings(actorSystem)
                .withMaxRetries(maxRetries)
                .withConnectionSettings(
                    ClientConnectionSettings(actorSystem)
                        .withTransport(httpsProxyTransport)
                        .withConnectingTimeout(connectionTimeout.seconds)
                )
        Http().singleRequest(HttpRequest(uri = targetUri), settings = settings)
    }
    
    def getResponse(targetUri:String)(implicit proxy:ProxyAgent = null,
                                          actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer) = {
        val settings = ConnectionPoolSettings(actorSystem)
            .withMaxRetries(maxRetries)
            .withConnectionSettings(
                ClientConnectionSettings(actorSystem)
                    .withConnectingTimeout(connectionTimeout.seconds)
            )
        if (proxy != null){
            settings.withTransport(
                ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxy.host,proxy.port))
            )
        }
        Http().singleRequest(HttpRequest(uri = targetUri), settings = settings)
    }
    
    def downloadRowData(targetUri:String)(implicit proxy:ProxyAgent = null,
                 actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer) = {
        
        getResponse(targetUri).flatMap( resp =>
            resp.entity.toStrict(connectionTimeout.seconds).map(s=>s.data)
        )
    }
    
    def download(targetUri:String,codec:String = "UTF8")(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer) = {
        
        downloadRowData(targetUri).map(data => data.decodeString(codec))
    }
    
    def getHeaders(targetUri:String)(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer) = {
        
        getResponse(targetUri).map( resp => {
            resp.discardEntityBytes()
            resp.headers
        })
    }
    
    def rectifyHtml(data:ByteString, charsetDesc:String = "utf-8") = {
        var isCharsetAttrName:Boolean = true
        var contentValue:String = "text/html; charset=utf-8"
        var charsetOriginal:String = "utf-8"
        var html = htmlCleaner.clean(data.decodeString(charsetDesc))
        val metas = html.evaluateXPath("./head/meta").map(_.asInstanceOf[TagNode])
        val meta = metas.collectFirst({
            case m if m.getAttributesInLowerCase.containsKey("charset") => {
                isCharsetAttrName = true
                charsetOriginal = m.getAttributesInLowerCase.get("charset").toLowerCase()
                m
            }
            case m if m.getAttributesInLowerCase.values().filter(_.toLowerCase.replace(" ","").indexOf("charset=") >=0).size > 0 => {
                isCharsetAttrName = false
                contentValue = m.getAttributesInLowerCase.values()
                        .map(_.toLowerCase.replace(" ",""))
                        .filter(_.indexOf("charset=") >=0)
                        .head
                charsetOriginal = contentValue.split(";")
                        .filter(_.indexOf("charset")==0)
                        .head.replace("charset=","")
                m
            }
        })
        if (List("utf8","utf-8").contains(charsetOriginal)) {
            serializer.getAsString(html)
        }else{
            html = htmlCleaner.clean(data.decodeString(charsetOriginal))
            val transMeta = html.evaluateXPath("./head/meta").map(_.asInstanceOf[TagNode]).filter( t => {
                if (isCharsetAttrName)
                    t.getAttributesInLowerCase.containsKey("charset")
                else
                    t.getAttributesInLowerCase.values().filter(_.toLowerCase.replace(" ","").indexOf("charset=") >=0).size > 0
            }).head
            if (isCharsetAttrName) {
                transMeta.removeAttribute("charset" )
                transMeta.addAttribute("charset",charsetDesc)
            } else {
                transMeta.removeAttribute("content" )
                transMeta.addAttribute("content",contentValue.replace(charsetOriginal,charsetDesc))
            }
            serializer.getAsString(html)
        }
    }
    
}
