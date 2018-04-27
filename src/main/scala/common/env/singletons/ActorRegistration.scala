package common.env.singletons

import akka.actor.ActorRef
import common.tool.FuncUtil

import scala.collection.mutable
import scala.reflect.ClassTag

private[env] object ActorRegistration extends FuncUtil {
    
    private val registeredActors = mutable.HashMap[String,ActorRef]()

    def registerStuff(name:String,stuff:ActorRef) = {
        registeredActors.put(name,stuff)
        this
    }

    def findStuff[T:ClassTag] = {
        registeredActors.get(simpleVariantName[T])
    }
    
    
  
}
