import java.io.File
import java.net.InetSocketAddress
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.{ClientTransport, Http}
import akka.stream.{ActorMaterializer, ClosedShape, IOResult}
import akka.stream.scaladsl.{Broadcast, FileIO, Flow, Framing, GraphDSL, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import common.TActor

import scala.concurrent.Future
import scala.concurrent.duration._

object MainTest extends App  {
    
    implicit val actorSystem = ActorSystem("default",ConfigFactory.load())
//    implicit val dispatch = actorSystem.dispatcher
//    implicit val materializer = ActorMaterializer()
    
//    val proxyHost = "localhost"
//    val proxyPort = 3128
//    val httpsProxyTransport = ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(proxyHost, proxyPort))
//
//    val settings = ConnectionPoolSettings(actorSystem)
//        .withMaxRetries(0)
//        .withConnectionSettings(
//            ClientConnectionSettings(actorSystem)
////            .withTransport(httpsProxyTransport)
//                .withConnectingTimeout(FiniteDuration(5,TimeUnit.SECONDS))
//
//
//        )
//    Http().singleRequest(HttpRequest(uri = "http://akka.io"), settings = settings)

    val ta = actorSystem.actorOf(Props[TActor])
    
    ta ! ""
    ta ! ""
    ta ! ""
    actorSystem.stop(ta)
    ta ! ""
    ta ! ""
    ta ! ""
    
}