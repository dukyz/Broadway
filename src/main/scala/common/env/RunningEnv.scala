package common.env

import akka.actor.ActorRef
import akka.stream.ActorMaterializer
import akka.util.Timeout
import common.env.singletons.{ActorRegistration, AkkaSystem, Cassandra}

import scala.concurrent.duration._
import scala.reflect.ClassTag

trait RunningEnv extends ConfigEnv {
    
    implicit val timeout = Timeout(3 seconds)
    
    implicit val actorSystem = AkkaSystem.actorSystem
    implicit val executionContext = AkkaSystem.defaultDispatcher
    implicit val materializer = ActorMaterializer()
    
    val cassandraSession = Cassandra.cassandraSession
    val cassandraSessionAsync = Cassandra.cassandraSessionAsync
    
    
    def registerStuff(name:String,stuff:ActorRef) = ActorRegistration.registerStuff(name,stuff)
    
    def findStuff[T:ClassTag] = ActorRegistration.findStuff[T]


}
