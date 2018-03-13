package actor.proxy

import akka.actor.{Actor, Timers}
import common.env.RunningEnv
import common.util.ActorUtil

import scala.concurrent.duration._

class ProxyGraber extends Actor with ActorUtil with Timers {
    
    private case object GRAB
    private case object KEY_OF_TIMER
    
    override def preStart = {
        timers.startPeriodicTimer(KEY_OF_TIMER,GRAB,classConfig.getInt("timerPeriodic").seconds)
    }
    
    override def receive = {
        case GRAB => grabNewProxy
        case _ =>
    }
    
    def grabNewProxy = {
    
        withUrl(classConfig.getString("url")){
            buff => buff.getLines().foreach(println)
        }
    }
}
