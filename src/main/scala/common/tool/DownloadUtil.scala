package common.tool

import java.net.InetSocketAddress
import java.nio.charset.Charset

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}
import akka.stream.Materializer
import akka.util.ByteString
import common.env.RunningEnv
import common.model.ProxyAgent
import org.htmlcleaner._

import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait DownloadUtil extends RunningEnv{
    private val http = Http()
    private val maxRetries = baseConfig.getInt("default.common.tool.DownloadUtil.max-retries")
    private val connectionTimeout = baseConfig.getInt("default.common.tool.DownloadUtil.connection-timeout")
    private val depthDefault =  baseConfig.getInt("default.common.tool.DownloadUtil.search-depth-when-301")
    val htmlCleaner = new HtmlCleaner()
    htmlCleaner.getProperties().setOmitXmlDeclaration(true)
    val htmlTagNodeSerializer = new CompactHtmlSerializer(htmlCleaner.getProperties())
    

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

    def getResponse(urlTarget:String)(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer):Future[HttpResponse] = {
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
        Http().singleRequest(HttpRequest(uri = urlTarget), settings = settings)
    }
    
    /**
      *
      * @param urlTarget Original url you got.Maybe the response statusCode will be 3xx(Redirect).So in this case
      *                  you have to re-request the "Location in Response Header"
      * @param depth  When you meet statusCode 3xx. "depth" decides how many times you could re-request.
      *               0 means No redirect
      *               n (n>0) means redirect at most n time
      * @param proxy
      * @param actorSystem
      * @param executeContent
      * @param materializer
      * @return
      */
    def downloadWithRelocate(urlTarget:String,depth:Int = depthDefault)(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer)
                    :Future[Tuple2[String,ByteString]] = {
        
        var realTarget = urlTarget
        
        def relocate(resp:HttpResponse) = {
            realTarget = resp.headers.filter(_.name()=="Location").head.value()
            resp.discardEntityBytes()
            downloadWithRelocate(realTarget,depth - 1)
        }
        
        getResponse(urlTarget).flatMap( resp => {
            //Maybe there will be other code status in future beside 301.So use "match" instead of "if else"
            resp.status.intValue() match {
                //same as condition "_" ,but should judge first
                case x:Int if depth < 1 => {
                    resp.entity.toStrict(connectionTimeout seconds).flatMap(s =>
                        Future {Tuple2(realTarget,s.data)}
                    )
                }
                //301 Moved Permanently
                case 301 => relocate(resp)
                //302 Found
                case 302 => relocate(resp)
                //same as condition "leftLevel < 1" ,but should judge last.
                case _ => {
                    resp.entity.toStrict(connectionTimeout seconds).flatMap(s =>
                        Future {Tuple2(realTarget,s.data)}
                    )
                }
            }
        })
    }
    
    /**
      * Download entity of html as String without redirect
      * @param urlTarget url to download
      * @param codec charset to decode to String
      * @param proxy
      * @param actorSystem
      * @param executeContent
      * @param materializer
      * @return
      */
    def downloadHtmlString(urlTarget:String,codec:String="utf-8")(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer):Future[String] = {
        
        downloadRawData(urlTarget).map(d => d.decodeString(codec))
    }
    
    /**
      * Download entity of html as ByteString without redirect
      * @param urlTarget url to download
      * @param proxy
      * @param actorSystem
      * @param executeContent
      * @param materializer
      * @return
      */
    def downloadRawData(urlTarget:String)(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer):Future[ByteString] = {
        
        getResponse(urlTarget).flatMap(resp => {
            resp.entity.toStrict(connectionTimeout.seconds).map(s => s.data)
        })
    }
    
    /**
      * Only download html header,status,protocol parts. Discard the entity part without redirect
      * @param targetUrl url to download
      * @param proxy
      * @param actorSystem
      * @param executeContent
      * @param materializer
      * @return
      */
    def downloadHeadersStatusProtocol(targetUrl:String)(implicit proxy:ProxyAgent = null,
                actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer)
                    :Future[Tuple3[Seq[HttpHeader],StatusCode,HttpProtocol]] = {

        getResponse(targetUrl).map( resp => {
            resp.discardEntityBytes()
            (resp.headers,resp.status,resp.protocol)
        })
    }
    
    
    /**
      * decode the html ByteString by charsetDesc.
      * Unify the meta attribute for charset.
      * <#meta charset="charsetDesc">
      * <#meta http-equiv="content-type" content="text/html;charset=charsetDesc">
      * Correct the html syntax.
      * @param htmlOriginal html as ByteString
      * @param charsetDesc target charset to transform
      * @return
      */
    def rectifyHtml(htmlOriginal:ByteString, charsetDesc:String = "utf-8"):String = {
        var isCharsetAttrName:Boolean = true
        var contentValue:String = "text/html; charset=utf-8"
        var charsetOriginal:String = "utf-8"
        var html = htmlCleaner.clean(htmlOriginal.decodeString(charsetDesc))
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
        if (Charset.forName(charsetOriginal) == Charset.forName(charsetDesc)) {
            htmlTagNodeSerializer.getAsString(html)
        }else{
            html = htmlCleaner.clean(htmlOriginal.decodeString(charsetOriginal))
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
            htmlTagNodeSerializer.getAsString(html)
        }
    }

}
