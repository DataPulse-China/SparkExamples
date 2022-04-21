package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.jfree.data.general.DefaultPieDataset

/**
 * 使用用户已经评分的电影训练模型推荐电影给用户
 */
object Ml100kTest {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)
    val value: RDD[String] = context.textFile("ml-100k/u.data")
    val ratings: RDD[Rating] = value
      .map(_.split("\t").take(3))
      .map {
        case Array(user, movie, rating) => Rating(user.toInt, movie.toInt, rating.toDouble)
      }
    val model: MatrixFactorizationModel = ALS.train(ratings, 50, 10, 0.01)
    val value1: RDD[(Int, Array[Rating])] = model.recommendProductsForUsers(10)
    value1.map {
      case (a, b) => (a, b.toList)
    }
      .collect()
      .foreach(println)
    context.stop()
  }
}
