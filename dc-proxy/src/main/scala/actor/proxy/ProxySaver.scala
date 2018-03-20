package actor.proxy

import akka.actor.Actor
import com.datastax.driver.core.PreparedStatement
import common.tool.ActorUtil

object ProxySaver {
    /**
      * Caution !!!!!
      * You should never never never change [defaultPartitionNo] after table [proxy.pool] start to hold data.
      * Or you will never find you data correctly !!!!
      */
    private[proxy] val poolDefaultPartitionNo = 1
    /**
      * Caution !!!!!
      * You should never never never change [archivePartitionNum] after table [proxy.archive] start to hold data.
      * Or you will never find you data correctly !!!!
      */
    private[proxy] val archivePartitionNum = 1000
    case class saveNewIntoArchive(order_id:Int,proxy:String)
    case class saveCheckedIntoPool(ts:Long,order_id:Int,proxy:String)
}

/**
  * @todo save the proxy into db
  *
  * @author dukyz
  */

class ProxySaver extends Actor with ActorUtil {
    import ProxySaver._
    
    private var preparedSaveNew:PreparedStatement = null
    private var preparedSaveChecked:PreparedStatement = null
    
    override def preStart() = {
        preparedSaveNew = cassandraSession.prepare(
            "insert into proxy.archive(partition_id,order_id,proxy) values (?,?,?)"
        )
    
        preparedSaveChecked = cassandraSession.prepare(
            "insert into proxy.pool(partition_id,ts,order_id,proxy) values (?,?,?,?)"
        )
    }
    
    override def receive = {
        case saveNewIntoArchive(order_id,proxy) => saveNewIntoArchive(order_id,proxy)
        case saveCheckedIntoPool(ts,order_id,proxy) => saveCheckedIntoPool(ts,order_id,proxy)
        case _ =>
    }
    
    def saveNewIntoArchive(order_id:Int,proxy:String) = {
        
        cassandraSession.execute(
            preparedSaveNew.bind()
                .setInt(0,(order_id / archivePartitionNum))
                .setInt(1,order_id)
                .setString(2,proxy)
        )
    }
    
    def saveCheckedIntoPool(ts:Long,order_id:Int,proxy:String) = {
        cassandraSession.execute(
            preparedSaveChecked.bind()
                .setInt(0,poolDefaultPartitionNo)
                .setLong(1,ts)
                .setInt(2,order_id)
                .setString(3,proxy)
        )
    }
}
