name := "dc-env"

version := "1.0"

scalaVersion := "2.11.12"

lazy val env = (project in file("."))

lazy val proxy = (project in file("dc-proxy")).dependsOn(env)

lazy val downloader = (project in file("dc-downloader")).dependsOn(env)

lazy val extractor = (project in file("dc-extractor")).dependsOn(env)

libraryDependencies ++= Dependencies.libDependencies
