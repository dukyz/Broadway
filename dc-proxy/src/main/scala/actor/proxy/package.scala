package actor

import akka.actor.{ActorRef, Props}
import common.env.PackageEnv

import scala.collection.mutable

package object proxy extends PackageEnv{
    
    protected def init = {
        actorSystem.actorOf(Props[ProxyManager],"proxyManager")
    }
    
    
    
    
    
    
}
