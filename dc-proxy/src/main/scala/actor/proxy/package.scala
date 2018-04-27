package actor

import akka.actor.{ActorRef, Props}
import common.env.EntryEnv

import scala.collection.mutable

/**
  * @todo the entry point of the module proxy
  *
  * @author dukyz
  */
package object proxy extends EntryEnv{
    
    protected def init = {
        actorSystem.actorOf(Props[ProxyManager],"proxyManager")
    }
    
    
    
    
    
    
}
