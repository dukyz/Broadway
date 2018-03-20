package actor.proxy

import akka.actor.Actor
import com.datastax.driver.core.PreparedStatement
import common.tool.ActorUtil

import scala.collection.JavaConversions.collectionAsScalaIterable

object ProxySeller   {
    case class Buy(n:Int)
}

class ProxySeller extends Actor with ActorUtil{
    import ProxySeller._
    
    private var preparedProxy:PreparedStatement = null
    
    override def preStart = {
        preparedProxy = cassandraSession.prepare(
            "select ts,order_id,proxy from proxy.pool where partition_id = ? order by ts limit ?"
        )
    }
    
    override def receive = {
        case Buy(n) => sender ! getProxy(n)
        case _ =>
    }
    
    def getProxy(n: Int): Set[(Int,String)] = {
        cassandraSession.execute(
            preparedProxy.bind()
                .setInt(0, ProxySaver.poolDefaultPartitionNo)
                .setInt(2, n)
        ).all.map(row =>
            (row.getInt("order_id"), row.getString("proxy"))
        ).toSet
    }
    
}
