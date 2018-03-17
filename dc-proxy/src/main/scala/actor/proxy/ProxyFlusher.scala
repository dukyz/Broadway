package actor.proxy

import akka.actor.{Actor, Timers}
import common.tool.ActorUtil

import scala.concurrent.duration._

class ProxyFlusher extends Actor with ActorUtil with Timers {
    
    private var flushOrder = 0
    private case object FLUSH
    private case object KEY_OF_TIMER
    
    override def preStart = {
        timers.startPeriodicTimer(KEY_OF_TIMER,FLUSH,classConfig.getInt("timerPeriodic") seconds)
    }
    
    override def receive = {
        case FLUSH => flushProxy
        case _ =>
    }
    
    def flushProxy = {
        cassandraSession.execute(s"select * from proxy.archive where order >= $flushOrder limit 1000")
    }
}
