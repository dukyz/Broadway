package actor.crawler

import akka.actor.{Actor, ActorRef, Props}
import com.datastax.driver.core.PreparedStatement
import common.setting.ShardingDefault.EntityEnvelope
import common.tool.ActorUtil

import scala.collection.JavaConversions.asScalaSet

/**
  * @todo Receive the word and dispatch the word to the search engines
  *
  * @author dukyz
  */

object WordDispatcher {
    
    /**
      * The case class for keyword
      * @param name the word which intend to search
      */
    case class Word(name:String)
}

class WordDispatcher extends Actor with ActorUtil{
    import WordDispatcher._
    
    //the search engines
    private var searchEngines:Map[String,ActorRef] = null
    //searchEngine classes location
    private val seClasspath = classConfig.getString("searchengine_classpath")
    private val saveWord:PreparedStatement = cassandraSession.prepare("insert into crawler.word(main_word) values(?)")
    
    override def preStart(): Unit = {
    
        //generate sharding actor for each search engine which has been configured in application.conf
        //these sharding actors need not been registered in actorRegistration,cause they should only be refered in WordDispatcher
        searchEngines =
            classConfig.getObject("searchengine").unwrapped().keySet().map {
                seName => (seName -> generateShardingActor(
                    Props(Class.forName(s"$seClasspath.$seName.Searcher")),
                    seName,
                    classConfig.getInt(s"searchengine.$seName.Searcher.concurrent_count"))
                    )
            }.toMap[String,ActorRef]

        self ! Word("完美陌生人")
    
    }
    
    
    override def receive: Receive = {
        
        case Word(name) => saveAndSearch(name)
        case _ =>
    }
    
    def saveAndSearch(word: String) = {
        
        cassandraSession.execute(saveWord.bind(word))
        searchEngines.foreach( _._2 ! EntityEnvelope(word) )
        
    }
    
}

