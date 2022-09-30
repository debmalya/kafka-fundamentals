ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.9"

lazy val root = (project in file("."))
  .settings(
    name := "spark-streaming-kafka"
  )


val sparkVersion = "3.3.0"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "3.2.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.4",
  "org.apache.flink" % "flink-connector-kafka" % "1.15.2",
  "org.apache.flink" % "flink-streaming-java" % "1.15.2",

//  "org.slf4j" % "slf4j-log4j12" % "2.0.3",
//  "log4j" % "log4j" % "1.2.17",

  // logging
  "org.apache.logging.log4j" % "log4j-api" % "2.4.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.4.1",


  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,

  "org.apache.spark" %% "spark-catalyst" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,

  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,


  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core"   % "2.12.0",
  "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.12.0",



)