package actor

import actor.crawler.WordDispatcher.Word
import akka.actor.Props
import common.env.PackageEnv

/**
  * @todo the entry point of the module crawler
  *
  * @author dukyz
  */
package object crawler extends PackageEnv{
    def init = {
        actorSystem.actorOf(Props[CrawlerManager],"crawlerManager")
    }
}
