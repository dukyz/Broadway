package actor

import akka.actor.Props
import common.env.PackageEnv

package object proxy extends PackageEnv{
    
    protected def init = {
        actorSystem.actorOf(Props[ProxyManager],"proxyManager")
    }
    
    
    
    
    
    
}
