package actor

import akka.actor.{ActorRef, Props}
import common.env.PackageEnv

import scala.collection.mutable

/**
  * @todo the entry point of the module proxy
  *
  * @author dukyz
  */
package object proxy extends PackageEnv{
    
    protected def init = {
        actorSystem.actorOf(Props[ProxyManager],"proxyManager")
    }
    
    
    
    
    
    
}
