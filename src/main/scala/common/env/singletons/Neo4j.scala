package common.env.singletons

import akka.event.LoggingAdapter
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session, Config => neo4jConfig}

private[env] object Neo4j {
    
    import Config.baseConfig
    
    private val port = baseConfig.getInt("default.common.env.singletons.Neo4j.port")
    private val host = baseConfig.getString("default.common.env.singletons.Neo4j.host")
    private val user = baseConfig.getString("default.common.env.singletons.Neo4j.user")
    private val password = baseConfig.getString("default.common.env.singletons.Neo4j.password")
    
    private val config = neo4jConfig.build()
        .withMaxConnectionPoolSize(baseConfig.getInt("default.common.env.singletons.Neo4j.connection-pool-size"))
        .toConfig()
    
    private val neo4j = GraphDatabase.driver(s"bolt://$host:$port", AuthTokens.basic(user, password),config)
    
    
    def withNeo4jSession(dealWithNeo4jSession:Session => Unit)(implicit log:LoggingAdapter=null) = {
        var s:Session = null
        try {
            s = neo4j.session()
            dealWithNeo4jSession(s)
        }catch {
            case e:Exception => if (log eq null) println(e.getMessage) else log.warning(e.getMessage)
        }finally {
            if (s != null)
                s.close()
        }
    }
    
    
    
        
    
    
 
}
