package common.env.singletons

import com.typesafe.config.ConfigFactory

private[env] object Config {
    val baseConfig = ConfigFactory.load()
}
