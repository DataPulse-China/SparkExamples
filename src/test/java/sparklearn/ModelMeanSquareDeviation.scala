package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, lit, mean, pow, sqrt}
import org.apache.spark.sql.{DataFrame, DataFrameStatFunctions, Dataset, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

/**
 * 求模型的总体均方差
 */
object ModelMeanSquareDeviation {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    val session: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("test11")
      .getOrCreate()

    import session.implicits._

    val ratings: Dataset[Rating] = session.read.textFile("ml-100k/u.data")
      .map(_.split("\t"))
      .map {
        case Array(user, product, rating, _) => Rating(user.toInt, product.toInt, rating.toDouble)
      }
    val model: MatrixFactorizationModel = ALS.train(ratings.rdd, 150, 10, 0.01)
    val value: RDD[Rating] = model.predict(ratings.map(o => (o.user, o.product)).rdd)

    val frame: DataFrame = value.toDF("user", "product", "rating")
      .as("a")
      .join(ratings.as("b"), col("a.user") === col("b.user") && col("a.product") === col("b.product"))
      .withColumn("SquareError", pow(col("b.rating") - col("a.rating"), lit(2)))

    frame.groupBy()
      .agg(mean(col("SquareError")).as("SquareErrorMean"))
      .show()
    session.stop()
  }
}
