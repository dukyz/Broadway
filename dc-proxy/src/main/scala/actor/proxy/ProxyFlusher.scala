package actor.proxy

import akka.actor.Timers
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.datastax.driver.core.PreparedStatement
import common.setting.ShardingDefault
import common.tool.ActorUtil

import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.concurrent.duration._

object ProxyFlusher {
    /**
      * Trigger object to flush proxy archive
      */
    case object Flush
}

class ProxyFlusher extends Timers with PersistentActor with ActorUtil  {
    import ProxyFlusher._
    
    override def persistenceId: String = self.path.toString
    
    private case object TIMER_OF_PROXYFLUSHER
    private var flushPartition_id:Int = -1
    private var preparedFlush:PreparedStatement = null
    private var preparedFindMaxPartition:PreparedStatement = null
    
    override def preStart = {
        preparedFlush = cassandraSession.prepare(
            "select proxy,order_id from proxy.archive where partition_id = ?"
        )
        preparedFindMaxPartition = cassandraSession.prepare(
            "select max(partition_id) from proxy.archive"
        )
    }
    
    override def receiveRecover: Receive = {
        case RecoveryCompleted => timers.startSingleTimer(TIMER_OF_PROXYFLUSHER,Flush,5 seconds)
        case pos:Int => this.flushPartition_id = pos
    }
    
    override def receiveCommand: Receive = {
        case Flush => flushProxy
        case _ =>
    }
    
    def flushProxy = {
        //increase partition_id
        flushPartition_id += 1
        //persistent partition_id
        persist(flushPartition_id){
            pid => {
                //send proxy in the partition to ProxyChecker
                cassandraSession.execute(preparedFlush.bind()
                    .setInt(0,pid)
                ).all()
                    .foreach(
                        row => {
                            val ts = System.currentTimeMillis()
                            actorRegistration.findStuff[ProxyChecker].get !
                                ShardingDefault.EntityEnvelope(
                                    ts.toString,ProxyChecker.Check(ts,row.getInt("order_id"),row.getString("proxy"))
                                )
                        }
                    )
                //delete history position
                deleteMessages(toSequenceNr = lastSequenceNr - 1)
                
                //if pid reach the max partition_id ,then reset flushPartition_id to -1,to start flush next turn
                (actorRegistration.findStuff[ProxyGraber].get ? ProxyGraber.FindMaxOrder)
                    .mapTo[Int].foreach(
                        oid => {
                            if (oid/ProxySaver.archivePartitionNum <= pid){
                                flushPartition_id = -1
                            }
                            timers.startSingleTimer(TIMER_OF_PROXYFLUSHER,Flush,classConfig.getInt("timerPeriodic") seconds)
                        }
                    )
            }
            
        }
        
    }
    
}
