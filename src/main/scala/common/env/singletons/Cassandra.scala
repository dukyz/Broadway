package common.env.singletons

import com.datastax.driver.core.{Cluster, HostDistance, PoolingOptions}

import scala.collection.JavaConversions.collectionAsScalaIterable

private[env] object Cassandra {
    
    import Config.baseConfig
    
    private val port = baseConfig.getInt("default.common.env.singletons.Cassandra.port")
    private val hosts = baseConfig.getStringList("default.common.env.singletons.Cassandra.hosts")
    private val coreNum = Runtime.getRuntime.availableProcessors()
    private val poolingOptions = new PoolingOptions()
        .setCoreConnectionsPerHost(HostDistance.LOCAL,coreNum)
        .setMaxConnectionsPerHost(HostDistance.LOCAL,coreNum * 4)
        .setCoreConnectionsPerHost(HostDistance.REMOTE,1)
        .setMaxConnectionsPerHost(HostDistance.REMOTE,4)
    private val cassandraCluster = Cluster.builder()
        .addContactPoints(hosts.toSeq:_*)
        .withPort(port)
        .withPoolingOptions(poolingOptions)
        .build()
        .init()
    
    val cassandraSession = cassandraCluster.connect()
    
    val cassandraSessionAsync = cassandraCluster.connectAsync()
    
}
