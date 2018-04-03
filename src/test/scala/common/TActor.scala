package common

import akka.actor.Actor

class TActor extends Actor {
    
    def receive = {
        case _ => Thread.sleep(2000);println(111);
    }
}
