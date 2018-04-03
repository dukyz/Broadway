package actor.crawler

import akka.actor.{Actor, ActorRef}
import common.tool.ActorUtil

class CrawlerManager extends Actor with ActorUtil{
    
    var keywordDispatcher:ActorRef = null
    var downloadExecutor:ActorRef = null
    var urlCollector:ActorRef = null
    var missingChecker:ActorRef = null
    var htmlCleaner:ActorRef = null
    
    override def preStart = {
        keywordDispatcher = generateSingletonActor[KeywordDispatcher]
//        urlCollector = generateSingletonActor[UrlCollector]
//        downloadExecutor = generateSingletonActor[DownloadExecutor]
//        missingChecker = generateSingletonActor[MissingChecker]
//        htmlCleaner = generateSingletonActor[HtmlCleaner]
        
        actorRegistration.registerStuff("crawlerManager",context.self)
        .registerStuff("keywordDispatcher",keywordDispatcher)
//        .registerStuff("urlCollector",urlCollector)
//        .registerStuff("downloadExecutor",downloadExecutor)
//        .registerStuff("missingChecker",missingChecker)
//        .registerStuff("htmlCleaner",htmlCleaner)
    }
    
    override def receive: Receive = {
        case _ =>
    }
}
