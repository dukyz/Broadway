package common.env.singletons

import akka.actor.ActorRef
import common.tool.EasyFunc

import scala.collection.mutable
import scala.reflect.ClassTag

private[env] object ActorRegistration extends EasyFunc {
    
    private val registeredActors = mutable.HashMap[String,ActorRef]()

    def registerStuff(name:String,stuff:ActorRef) = {
        registeredActors.put(name,stuff)
    }

    def findStuff[T:ClassTag] = {
        registeredActors.get(simpleVariantName[T])
    }
    
    
  
}
