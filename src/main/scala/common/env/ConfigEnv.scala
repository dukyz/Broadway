package common.env

import com.typesafe.config.ConfigFactory
import common.env.singletons.Config

import scala.util.Try

trait ConfigEnv {
    val baseConfig = Config.baseConfig
    val classConfig = Try(baseConfig.getConfig("default."+this.getClass.getName.replace("$","")))
        .getOrElse(ConfigFactory.empty())
}
