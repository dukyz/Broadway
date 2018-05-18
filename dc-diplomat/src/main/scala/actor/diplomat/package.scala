package actor

import actor.crawler.WordDispatcher
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.AskSupport
import common.env.EntryEnv

import scala.util.{Failure, Success}

package object diplomat extends EntryEnv with AskSupport{
    
    val gets = get {
        path("item") {
            complete(StatusCodes.NotFound)
        } ~ path("asdf") {
            complete(StatusCodes.NotFound)
        }
    }
    
    val posts = post {
        //curl -X POST http://127.0.0.1:8080/word --data 复联三
        path("word") {
            entity(as[String]){
                word =>
                    onComplete(findStuff[WordDispatcher].get ? WordDispatcher.Word(word)){
                        case Success(s) => complete(StatusCodes.Created)
                        case Failure(f) => complete(f.getMessage)
                    }
            }
        }
    }
    
    val port = classConfig.getInt("port")
    val host = classConfig.getString("host")
    val routes = gets ~ posts
    val httpServer = Http().bindAndHandle(routes, host, port)
    
    def init = {
//        actorSystem.actorOf(Props[DiplomatManager],"diplomatManager")
    }

}
