package actor

import akka.actor.Actor
import common.util.ActorUtil

class TestActor extends Actor with ActorUtil{
    override def receive: Receive = {
        case _ =>
    }
}
