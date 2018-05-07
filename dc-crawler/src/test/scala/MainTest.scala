import akka.actor.{Actor, ActorSystem}

object MainTest extends App {
    
//    val tpe = universe.typeOf[Actor]
//    val clazz = tpe.typeSymbol.asClass
//    // if you want to ensure the type is a sealed trait,
//    // then you can use clazz.isSealed and clazz.isTrait
//    clazz.knownDirectSubclasses.foreach(println)
    
    
//    import org.reflections.Reflections
//    import scala.collection.JavaConversions.asScalaSet
//    val b = System.currentTimeMillis()
//    val reflections = new Reflections("actor")
    
//    val allClasses = reflections.getSubTypesOf(classOf[common.env.singletons.HttpServer.RestFul]).foreach(println)
    
//    println(System.currentTimeMillis() - b)
//    val reflections = new Reflections()
    
//    def findAllObjects[T](cl: Class[T])= {
//        reflections.getSubTypesOf()
//    }
    
//    findAllObjects(classOf[C2]).foreach(println)
















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
//    Source.fromFile("/home/dukyz/Downloads/WM.html","GB2312").getLines().foreach(println)
//    println(pattern(str))
//    val b  = "text/html; charset= UTF-8".replace(" ","").split(";").filter(_.indexOf("charset")==0).head.replace("charset=","")
//    val c  = "text/html; charset=UTF-8".replaceAll("charset=(UTF-?8|GB.*)","charset=utf8")
//    println(b)
//    println(c)
    
//    Http().bindAndHandle(null, "localhost", 8080)
    
//    class XXX {
//    }
//
//    object XXX {
//        def apply(f:Int=>String): XXX = new XXX()
//    }
//
//
//
//    def get:XXX = {
//        new XXX()
//    }
//
//    val x = get
//
//    val y = x()
//
//    y

}

