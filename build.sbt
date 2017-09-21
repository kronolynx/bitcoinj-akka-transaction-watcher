import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.github",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "watchwallet",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "org.bitcoinj" % "bitcoinj-core" % "0.14.5",
      "com.typesafe.akka" % "akka-slf4j_2.12" % "2.5.4",
      "com.typesafe.akka" %% "akka-actor" % "2.5.4",
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    )

  )
