package actor

import actor.crawler.WordDispatcher.Word
import akka.actor.Props
import common.env.EntryEnv

/**
  * @todo the entry point of the module crawler
  *
  * @author dukyz
  */
package object crawler extends EntryEnv{
    def init = {
        actorSystem.actorOf(Props[CrawlerManager],"crawlerManager")
    }
}
