package common.env.singletons

private[env] object AkkaSystem {
    import Config.baseConfig
    import akka.actor.ActorSystem
    
    val actorSystem = ActorSystem(baseConfig.getString("akka.cluster.name"), baseConfig)
    val defaultDispatcher = actorSystem.dispatcher
}
