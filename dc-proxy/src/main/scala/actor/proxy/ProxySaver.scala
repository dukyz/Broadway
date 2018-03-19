package actor.proxy

import actor.proxy.ProxySaver.{SaveChecked, SaveNew}
import akka.actor.Actor
import com.datastax.driver.core.PreparedStatement
import common.tool.ActorUtil

object ProxySaver {
    case class SaveNew(order_id:Int,proxy:String)
    case class SaveChecked(proxy:String)
}

/**
  * @todo save the proxy into db
  *
  * @author dukyz
  */
class ProxySaver extends Actor with ActorUtil {
    
    /**
      * Caution !!!!!
      * You should never never never change [archivePartitionNum] after table [proxy.archive] start to hold data.
      * Or you will never find you data correctly !!!!
      */
    private val archivePartitionNum = 50
    
    private var preparedSaveNew:PreparedStatement = null
    private var preparedSaveChecked:PreparedStatement = null
    
    override def preStart() = {
        preparedSaveNew = cassandraSession.prepare(
            "insert into proxy.archive(partition_id,order_id,proxy) values (?,?,?)"
        );
    
        preparedSaveChecked = cassandraSession.prepare(
            "insert into proxy.archive(partition_id,order_id,proxy) values (?,?,?)"
        );
    }
    
    override def receive = {
        case SaveNew(order_id,proxy) => saveNew(order_id,proxy)
        case SaveChecked(proxy) => saveChecked(proxy)
        case _ =>
    }
    
    def saveNew(order_id:Int,proxy:String) = {
        
        cassandraSession.execute(
            preparedSaveNew.bind()
            .setInt(0,order_id % archivePartitionNum)
            .setInt(1,order_id)
            .setString(2,proxy)
        )
    }
    
    def saveChecked(proxy:String) = {
        cassandraSession.execute(preparedSaveChecked.bind())
    }
    
}
