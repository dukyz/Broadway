package actor.crawler

import akka.actor.{Actor, ActorRef, Props}
import common.setting.ShardingDefault.EntityEnvelope
import common.tool.ActorUtil

import scala.collection.JavaConversions.asScalaSet

object KeywordDispatcher {
    case class Keyword(name:String)
}

class KeywordDispatcher extends Actor with ActorUtil{
    import KeywordDispatcher._
    var searchEngines:Map[String,ActorRef] = null
    
    override def preStart(): Unit = {
        val seClasspath = classConfig.getString("searchengine_classpath")
        
        searchEngines =
            classConfig.getObject("searchengine").unwrapped().keySet().map {
                seName => (seName -> generateShardingActor(
                    Props(Class.forName(s"$seClasspath.$seName.Searcher")),
                    seName,
                    classConfig.getInt(s"searchengine.$seName.Searcher.concurrent_count"))
                    )
            }.toMap[String,ActorRef]
    }
    
    override def receive: Receive = {
        
        case Keyword(name) => searchEngines.foreach( _._2 ! EntityEnvelope(name) )
        case _ =>
    }
    
}

