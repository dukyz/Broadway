package common.env.singletons

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

private[env] object HttpServer{
   
    import Config.baseConfig
    
    val httpServer =
        if (baseConfig.getBoolean("default.common.env.singletons.HttpServer.enable")) {
    
            implicit val actorSystem = AkkaSystem.actorSystem
            implicit val materializer = ActorMaterializer()
            val port = baseConfig.getInt("default.common.env.singletons.HttpServer.port")
            val host = baseConfig.getString("default.common.env.singletons.HttpServer.host")
            Http().bindAndHandle(getRoute, host, port)
        }else{
            throw new NotImplementedError("HttpServer has not been turn on.")
        }
    
    
    
    def getRoute:Route = {
    
        path("hello") {
            get {
//                ActorRegistration.findStuff[WordDis]
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
            }
        }
    }

}