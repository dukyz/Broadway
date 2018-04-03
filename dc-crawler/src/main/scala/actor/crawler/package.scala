package actor

import akka.actor.Props
import common.env.PackageEnv

package object crawler extends PackageEnv{
    def init = {
        actorSystem.actorOf(Props[CrawlerManager],"crawlerManager")
    }
}
