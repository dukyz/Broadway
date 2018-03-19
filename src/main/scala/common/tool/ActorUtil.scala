package common.tool

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import common.env.RunningEnv
import common.setting.ShardingDefault

import scala.reflect.ClassTag

/**
  * @todo This trait should be used with Actor only
  * @author dukyz
  */
trait ActorUtil extends RunningEnv with EasyFunc with ActorLogging{
    this:Actor =>
    
    /**
      * generate a general actor
      * @tparam T actor class
      * @return general actor
      */
    def generateNormalActor[T<:Actor:ClassTag]:ActorRef = {
        context.actorOf(Props[T],simpleVariantName[T])
    }
    
    
    /**
      * generate a singleton actor and return a proxy
      * @tparam T actor class
      * @return singleton actor proxy
      */
    def generateSingletonActor[T<:Actor:ClassTag]:ActorRef = {

        val propSingleton = ClusterSingletonManager
            .props(Props[T],PoisonPill,ClusterSingletonManagerSettings(actorSystem))
        val actorSingleton = context
            .actorOf(propSingleton,simpleVariantName[T])

        val propProxy = ClusterSingletonProxy
            .props(actorSingleton.path.toStringWithoutAddress,ClusterSingletonProxySettings(actorSystem))
        context.actorOf(propProxy,simpleVariantName[T]+"Proxy")
    }
    
    
    /**
      * generate a local shardRegion for entity
      * @tparam T
      * @return local shardRegion for entity
      */
    def generateShardingActor[T<:Actor:ClassTag](numberOfShards:Int=1):ActorRef = {
        
        val shardingDefault = ShardingDefault(numberOfShards)
        ClusterSharding(actorSystem).start(
            typeName = implicitly[ClassTag[T]].runtimeClass.getSimpleName,
            entityProps = Props[T],
            settings = ClusterShardingSettings(actorSystem),
            extractEntityId = shardingDefault.extractEntityId,
            extractShardId = shardingDefault.extractShardId
        )
    }
    
    
}



