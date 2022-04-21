package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, SparkSession}

import java.util.UUID

object KAvgAccuracyAPK {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.OFF)
    val session: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName(UUID.randomUUID().toString)
      .getOrCreate()

    import session.implicits._

    val ratings: Dataset[Rating] = session.read.textFile("ml-100k/u.data")
      .map(_.split("\t"))
      .map {
        case Array(a, b, c, _) => Rating(a.toInt, b.toInt, c.toDouble)
      }

    val model: MatrixFactorizationModel = ALS.train(ratings.rdd, 150, 20, 0.01)
    val value: RDD[Rating] = model.predict(ratings.map(o => (o.user, o.product)).rdd)
    //    value.collect().foreach(println)
    val userId = 789
    val K = 10
    val topKRecs: Array[Rating] = model.recommendProducts(userId, K)
    val moviesForUser: Seq[Rating] = ratings.rdd.keyBy(_.user).lookup(userId)

    //    val actualRating: Rating = moviesForUser.take(1).head
    //    用户评分过的电影
    val actualMovies: Seq[Int] = moviesForUser.map(_.product)
    //    预测到的电影
    val predictedMovies: Array[Int] = topKRecs.map(_.product)

    println(actualMovies)
    println("-------------------------")
    println(predictedMovies.toList)
    println("-------------------------")

    /**
     * @param actual    实际的
     * @param predicted 预料到的
     * @param k         预测的个数
     * @return 准确率
     */
    def avgPrecisionK(actual: Seq[Int], predicted: Seq[Int], k: Int): Double = {
      //  去除预估的前k个
      val predK: Seq[Int] = predicted.take(k)
      //    成绩
      var score = 0.0
      //    点数
      var numHits = 0.0
      for ((p, i) <- predK.zipWithIndex) {
        if (actual.contains(p)) {
          //   如果实际中包含预测到的
          //    点数+1
          numHits += 1.0
          //   成绩 += 点数 / (索引+1)
          score += numHits / (i.toDouble + 1.0)
          println(s"score = $score, numHits = $numHits")
        } else {
          println(s"不存在 === ${p}")
        }
      }
      //  如果实际值为空返回1
      if (actual.isEmpty) {
        1.0
      } else {
        //    成绩除以 / min(真实数据,k)
        score / scala.math.min(actual.size, k).toDouble
      }
    }

    val apk10: Double = avgPrecisionK(actualMovies, predictedMovies, K)
    println(apk10)

    session.stop()
  }
}
