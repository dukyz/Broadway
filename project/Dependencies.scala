import sbt._

object Dependencies {
    val akkaHttpVersion = "10.1.0"
    val akkaVersion = "2.5.11"
    val libDependencies = Seq(
        "com.typesafe.akka"     %% "akka-http"      % akkaHttpVersion,
        "com.typesafe.akka"     %% "akka-cluster"           % akkaVersion,
        "com.typesafe.akka"     %% "akka-cluster-sharding"           % akkaVersion,
        "com.typesafe.akka"     %%  "akka-cluster-tools"    %   akkaVersion,
        "com.typesafe.akka"     %% "akka-distributed-data"           % akkaVersion,
        "com.typesafe.akka"     %% "akka-persistence" % akkaVersion,
        "com.typesafe.akka"     %% "akka-persistence-query" % akkaVersion,
        "com.typesafe.akka"     %% "akka-persistence-cassandra" % "0.80",
        "edu.uci.ics"           % "crawler4j"       % "4.3"
        
    )
}
