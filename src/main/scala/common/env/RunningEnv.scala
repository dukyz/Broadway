package common.env

import akka.util.Timeout
import common.env.singletons.{ActorRegistration, AkkaSystem, Cassandra}

import scala.concurrent.duration._

trait RunningEnv extends ConfigEnv {
    
    implicit val timeout = Timeout(3.seconds)
    implicit val actorSystem = AkkaSystem.actorSystem
    implicit val cassandraSession = Cassandra.cassandraSession
    implicit val cassandraSessionAsync = Cassandra.cassandraSessionAsync
    
    val actorRegistration = ActorRegistration
    


}
