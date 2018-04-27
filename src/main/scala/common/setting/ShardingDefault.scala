package common.setting

import akka.cluster.sharding.ShardRegion
import common.tool.{FuncUtil, Empty}

object ShardingDefault {
    
    final case class EntityEnvelope(entityId: String , msg: Any = null)
    def apply(numberOfShards: Int = 1): ShardingDefault = new ShardingDefault(numberOfShards)
    
}

class ShardingDefault (numberOfShards:Int=1) extends Empty with FuncUtil  {
    import ShardingDefault._
    
    val extractEntityId:ShardRegion.ExtractEntityId = {
        case EntityEnvelope(entityId, msg) => (entityId, nvl(msg,entityId) )
    }
    
    val extractShardId:ShardRegion.ExtractShardId = {
        case EntityEnvelope(entityId, _) => (entityId.hashCode % numberOfShards).toString
        case ShardRegion.StartEntity(entityId) => (entityId.hashCode % numberOfShards).toString
    }
    
}
