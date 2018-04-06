package common.tool

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings, ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.pattern.AskSupport
import common.env.RunningEnv
import common.setting.ShardingDefault

import scala.reflect.ClassTag

/**
  * @todo This trait should be used with Actor only
  * @author dukyz
  */
trait ActorUtil extends RunningEnv with EasyFunc with ActorLogging with AskSupport{
    this:Actor =>
    
    /**
      * generate a general actor
      * @tparam T actor class
      * @return general actor
      */
    def generateNormalActor[T<:Actor:ClassTag]:ActorRef = {
        generateNormalActor(Props[T],simpleVariantName[T])
    }
    
    def generateNormalActor(props:Props,name:String = null):ActorRef = {
        context.actorOf(props,nvl(name,simpleVariantName(props.actorClass())))
    }
    

    /**
      * generate a singleton actor and return a proxy
      * @tparam T actor class
      * @return singleton actor proxy
      */
    def generateSingletonActor[T<:Actor:ClassTag]:ActorRef = {
        generateSingletonActor(Props[T],simpleVariantName[T])
    }
    
    def generateSingletonActor(props:Props,name:String = null):ActorRef = {
        val propSingleton = ClusterSingletonManager
            .props(props,PoisonPill,ClusterSingletonManagerSettings(actorSystem))
        
        val singletonName = nvl(name,simpleVariantName(props.actorClass()))
        val actorSingleton = context.actorOf(propSingleton,singletonName)
    
        val propProxy = ClusterSingletonProxy
            .props(actorSingleton.path.toStringWithoutAddress,ClusterSingletonProxySettings(actorSystem))
        context.actorOf(propProxy,singletonName+"Proxy")
    }
    
    /**
      * generate a local shardRegion for entity
      * @tparam T
      * @return local shardRegion for entity
      */
    def generateShardingActor[T<:Actor:ClassTag](numberOfShards:Int):ActorRef = {
        
        generateShardingActor(
            Props[T],
            implicitly[ClassTag[T]].runtimeClass.getSimpleName,
            numberOfShards
        )
    }
    
    def generateShardingActor(props:Props,name:String=null,numberOfShards:Int):ActorRef = {
    
        val shardingDefault = ShardingDefault(numberOfShards)
        ClusterSharding(actorSystem).start(
            typeName = nvl(name,props.actorClass().getSimpleName),
            entityProps = props,
            settings = ClusterShardingSettings(actorSystem),
            extractEntityId = shardingDefault.extractEntityId,
            extractShardId = shardingDefault.extractShardId
        )
    }
    
    
}



