package common.tool

import akka.event.LoggingAdapter

import scala.io.{BufferedSource, Codec, Source}
import scala.reflect.ClassTag


/**
  * This trait provides some funtion for your convenience
  * @author dukyz
  */
trait EasyFunc  {
    
    /**
      * generate a simple name(lower camel case) for variant from Class name(Upper camel case).
      * eg:ClassName => className
      * @tparam T any Class
      * @return variant name(lower camel case)
      */
    def simpleVariantName[T:ClassTag] = {
        val name = implicitly[ClassTag[T]].runtimeClass.getSimpleName
        name.head.toLower + name.drop(1)
    }
    
    /**
      * generate a simple name(lower camel case) for variant from Class name(Upper camel case).
      * eg:path.to.ClassName => className
      * @param c any Class
      * @return variant name(lower camel case)
      */
    def simpleVariantName(c:Class[_]) = {
        val name = c.getSimpleName
        name.head.toLower + name.drop(1)
    }
    
    /**
      * deal with url without forgetting closing resource
      * @param url
      * @param func
      * @param codec
      * @param log
      */
    def withUrl(url:String)(func:BufferedSource => Unit)(implicit codec:Codec=Codec.UTF8,log:LoggingAdapter=null) = {
        var s:BufferedSource = null
        try{
            s = Source.fromURL(url)
            func(s)
        }catch{
            case e:Exception => if (log eq null) println(e.getMessage) else log.warning(e.getMessage)
        }
        finally {
            if (s != null)
                s.close()
        }
    }
    
    
    /**
      * find the first not null object
      * @param list some object that we don't know which one is null
      * @tparam T Class of param
      * @return first not null object
      */
    def nvl[T](list:T*):T = {
        list.find(x => x != null).get
    }
}
