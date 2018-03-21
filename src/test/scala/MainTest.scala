import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object MainTest extends App  {
    
    implicit val actorSystem = ActorSystem("default",ConfigFactory.load())
    implicit val dispatch = actorSystem.dispatcher
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))
    
    responseFuture.onComplete( x => println(x.get.entity.dataBytes))
}