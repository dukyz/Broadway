

object MainTest extends App {
//    val config = ConfigFactory.load()
//    val path = "default.actor.crawler.KeywordDispatcher.searchengine"
//    implicit val actorSystem = ActorSystem.create("default",config)
//    implicit val executeContent = actorSystem.dispatcher
//    implicit val materializer = ActorMaterializer()
//
//    val KEYWORD = URLEncoder.encode("笑匠","utf8")
//    var url = s"http://www.baidu.com"
////    url = "https://baike.baidu.com/item/%E7%AC%91%E5%8C%A0/13856647?fr=aladdin"
//    url = s"http://www.baidu.com/s?q1=$KEYWORD&rn=50&lm=1"
//    val x = download(url)
//
//    x.foreach( stream => {
//
//        new HtmlCleaner().clean(stream).evaluateXPath("//a/@href").foreach(println(_))
//
//    })
//
//
//    def download(targetUri:String,proxyHost:String=null,proxyPort:Int=18080)
//                (implicit actorSystem: ActorSystem,executeContent:ExecutionContext,materializer:Materializer)= {
//        println(targetUri)
//        val settings = ConnectionPoolSettings(actorSystem)
//            .withMaxRetries(2)
//            .withConnectionSettings(
//                ClientConnectionSettings(actorSystem)
//                    .withConnectingTimeout(2.seconds)
//            )
//        Http().singleRequest(HttpRequest(uri = targetUri),settings=settings).map( resp =>
//            resp.entity.dataBytes.runWith(StreamConverters.asInputStream(2.seconds))
//        )
//    }
    val pattern = "dsf".r
    val str = "Scala is Scalable and cool"
    
//    println(pattern(str))
}

