package sparklearn

import breeze.linalg.DenseVector
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import breeze.linalg._

/**
 * 使用用户已经评分的电影训练模型推荐电影给用户
 */
object Ml100kTest2 {
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

    val model: MatrixFactorizationModel = ALS.train(ratings, 200, 10, 0.01)
    val features: RDD[(Int, Array[Double])] = model.productFeatures

    //    val v: DenseVector[Double] = DenseVector(features.lookup(567).head)

    //    features.map(o => {
    //      DenseVector(o._2).dot(v) / (norm(DenseVector(o._2)) * norm(v))
    //    })
    //      .collect()
    //      .sortBy(o => o)
    //      .foreach(println)


    features.join(features)
      .map {
        case (id, (a, b)) => (id, DenseVector(a).dot(DenseVector(b)) / (norm(DenseVector(a) * norm(DenseVector(b)))))
      }
      .collect()
      .foreach(println)
    context.stop()
  }
}
