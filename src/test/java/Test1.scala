import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object Test1 extends App {
  Logger.getLogger("org").setLevel(Level.WARN)
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Broadcast Test")
    .master("local[*]")
    .getOrCreate()

  spark.sparkContext.parallelize(1 until 100, 2)

  spark.close()
}
