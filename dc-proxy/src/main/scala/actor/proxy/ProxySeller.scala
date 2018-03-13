package actor.proxy

import akka.actor.Actor
import common.util.ActorUtil

object ProxySeller   {
    case object Increment
    case object Decrement
}

class ProxySeller extends Actor with ActorUtil{
    import ProxySeller._
    var count = 0
    
    override def receive = {
        case Increment => {
            count += 1
            println(s"count is $count")
//            (ClusterSharding(system).shardRegion("ProxySeller") ? ShardRegion.GetClusterShardingStats).onSuccess({
//                case x => println(x)
//            })
        }
        case Decrement => {
            count -= 1
            println(s"count is $count")
            
            
        }
    }
}
