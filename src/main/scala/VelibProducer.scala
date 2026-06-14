import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

import java.io.File

object VelibProducer {

  case class ProducerConfig(
    rowsPerSecond: Int = 1,
    outputPath: String = "C:/Users/koman/Downloads/Projet annuel/Real/temp/velib_data",
    checkpointPath: String = "C:/Users/koman/Downloads/Projet annuel/Real/temp/checkpoint",
    apiUrl: String = VelibUtils.DefaultApiUrl
  )

  def parseArgs(args: Array[String]): ProducerConfig = {
    val params = scala.collection.mutable.Map[String, String]()
    var i = 0
    while (i < args.length) {
      args(i) match {
        case "--rows-per-second" => params("rowsPerSecond") = args(i + 1); i += 2
        case "--output-path" => params("outputPath") = args(i + 1); i += 2
        case "--checkpoint-path" => params("checkpointPath") = args(i + 1); i += 2
        case "--api-url" => params("apiUrl") = args(i + 1); i += 2
        case _ => i += 1
      }
    }

    ProducerConfig(
      rowsPerSecond = params.getOrElse("rowsPerSecond", "1").toInt,
      outputPath = params.getOrElse("outputPath", ProducerConfig().outputPath),
      checkpointPath = params.getOrElse("checkpointPath", ProducerConfig().checkpointPath),
      apiUrl = params.getOrElse("apiUrl", VelibUtils.DefaultApiUrl)
    )
  }

  def main(args: Array[String]): Unit = {
    val config = parseArgs(args)

    new File(config.outputPath).mkdirs()
    new File(config.checkpointPath).mkdirs()

    val spark = SparkSession.builder
      .appName("Velib Streaming App")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val jsonStream = VelibUtils.createJsonStream(spark, config.rowsPerSecond, config.apiUrl)

    val consoleQuery = jsonStream.writeStream
      .outputMode("append")
      .format("console")
      .option("truncate", false)
      .trigger(Trigger.ProcessingTime("30 seconds"))
      .start()

    val fileQuery = jsonStream.writeStream
      .outputMode("append")
      .format("json")
      .option("path", config.outputPath)
      .option("checkpointLocation", config.checkpointPath)
      .trigger(Trigger.ProcessingTime("30 seconds"))
      .start()

    spark.streams.awaitAnyTermination()

    consoleQuery.stop()
    fileQuery.stop()
    spark.stop()
  }
}
