import org.apache.spark.sql.SparkSession
import VelibUtils._
import org.apache.spark.sql.streaming.Trigger

object VelibProducer {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Velib Streaming App")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val jsonStream = createJsonStream(spark)

    val query = jsonStream.writeStream
      .outputMode("append")
      .format("console")
      .start()

    jsonStream.writeStream
      .outputMode("append")
      .format("json")
      .option("path", "C:/Users/koman/Downloads/Projet annuel/Real/temp/velib_data")
      .option("checkpointLocation", "C:/Users/koman/Downloads/Projet annuel/Real/temp/checkpoint")
      .start()



    query.awaitTermination()
  }
}
