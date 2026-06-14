ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "Velib",
    Compile / run / mainClass := Some("Main")
  )

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
//libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
//libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
//libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.1"

//// https://mvnrepository.com/artifact/com.typesafe.akka/akka-http
//libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.2.10"

// https://mvnrepository.com/artifact/com.typesafe.play/play-json
//libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.4"

// https://mvnrepository.com/artifact/com.softwaremill.sttp.client3/core
//libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.8.3"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.3.1",
  "org.apache.spark" %% "spark-sql" % "3.3.1",
  //"org.apache.kafka" %% "kafka" % "2.8.1",
  //"com.softwaremill.sttp.client3" %% "core" % "3.8.3",
  //"org.json4s" %% "json4s-native" % "4.0.6",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "org.scalaj" %% "scalaj-http" % "2.4.2",

  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.1",
  "org.apache.kafka" % "kafka-clients" % "3.3.1" // Kafka version compatible
)


