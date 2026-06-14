import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scalaj.http.Http

import scala.util.{Failure, Success, Try}

object VelibUtils {

  val DefaultApiUrl = "https://data.opendatasoft.com/api/explore/v2.1/catalog/datasets/velib-disponibilite-en-temps-reel@parisdata/records?limit=100"

  val schema: StructType = StructType(Seq(
    StructField("total_count", IntegerType),
    StructField("results", ArrayType(StructType(Seq(
      StructField("stationcode", StringType),
      StructField("name", StringType),
      StructField("is_installed", StringType),
      StructField("capacity", IntegerType),
      StructField("numdocksavailable", IntegerType),
      StructField("numbikesavailable", IntegerType),
      StructField("mechanical", IntegerType),
      StructField("ebike", IntegerType),
      StructField("is_renting", StringType),
      StructField("is_returning", StringType),
      StructField("duedate", StringType),
      StructField("coordonnees_geo", StructType(Seq(
        StructField("lon", DoubleType),
        StructField("lat", DoubleType)
      ))),
      StructField("nom_arrondissement_communes", StringType),
      StructField("code_insee_commune", StringType),
    ))))
  ))

  def fetchDataFromAPI(apiUrl: String = DefaultApiUrl): String = {
    val finalUrl = if (apiUrl.contains("limit=")) apiUrl else s"$apiUrl&limit=100"
    Try(Http(finalUrl).timeout(30000, 60000).asString.body) match {
      case Success(body) => body
      case Failure(exception) =>
        println(s"Error fetching data: ${exception.getMessage}")
        "{}"
    }
  }

  def createJsonStream(spark: SparkSession, rowsPerSecond: Int = 1, apiUrl: String = DefaultApiUrl): DataFrame = {
    import spark.implicits._

    val data = spark.readStream
      .format("rate")
      .option("rowsPerSecond", rowsPerSecond.toString)
      .load()

    data.map(_ => fetchDataFromAPI(apiUrl))
      .select(from_json(col("value"), schema).as("data"))
      .select(explode(col("data.results")).as("station"))
      .select(
        col("station.stationcode"),
        col("station.name"),
        col("station.is_installed"),
        col("station.capacity"),
        col("station.numbikesavailable"),
        col("station.numdocksavailable"),
        col("station.mechanical"),
        col("station.ebike"),
        col("station.duedate"),
        col("station.coordonnees_geo.lon").as("longitude"),
        col("station.coordonnees_geo.lat").as("latitude"),
        col("station.nom_arrondissement_communes")
      )
  }
}
