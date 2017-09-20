import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Hello",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.bitcoinj" % "bitcoinj-core" % "0.14.5",
      "com.typesafe.akka" % "akka-slf4j_2.11" % "2.5.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    )

  )
