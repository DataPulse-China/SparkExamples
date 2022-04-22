package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import breeze.linalg._
import org.apache.spark.mllib.evaluation.RegressionMetrics
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, lit, mean, pow}

object Test5 {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.OFF)

    val session: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("test1")
      .getOrCreate()

    import session.implicits._

    val ratings: Dataset[Rating] = session.read.textFile("ml-100k/u.data")
      .map(_.split("\t"))
      .map {
        case Array(user, product, rating, _) => Rating(user.toInt, product.toInt, rating.toDouble)
      }

    val item: Dataset[Array[String]] = session.read.textFile("ml-100k/u.item")
      .map(_.split("\\|"))


    val movieframe: DataFrame = item.withColumn("mvid", col("value")(0))
      .withColumn("mvname", col("value")(1))
      .withColumn("mvyear", col("value")(2))
      .withColumn("mvurl", col("value")(4))
      .drop("value")

    val splitSet: Array[Dataset[Rating]] = ratings.randomSplit(Array(0.7, 0.3))

    //    模型
    val model: MatrixFactorizationModel = ALS.train(ratings.randomSplit(Array(0.7, 0.3)).head.rdd, 100, 10, 0.01)

    println("----------------  模型的因子数量  -----------------")
    println(s"userFeatures = ${model.userFeatures.count()} productFeatures = ${model.productFeatures.count()} ")

    println("-------------  617相似电影10部  -------------")
    /**
     * 计算余弦相似度
     *
     * @param o
     * @return
     */
    val denseMatrix617: DenseVector[Double] = DenseVector(model.productFeatures.lookup(617).head)

    def cosinePhaseVelocity(o: (Int, Array[Double])): (Int, Double) = {
      val value: DenseVector[Double] = DenseVector(o._2)
      (o._1, value.dot(denseMatrix617) / (norm(value) * norm(denseMatrix617)))
    }

    val recommendedMovies: Array[(Int, Double)] = model.productFeatures.map(cosinePhaseVelocity)
      .sortBy(-_._2)
      .take(10)

    session.createDataFrame(recommendedMovies)
      .toDF("mvid", "similarity")
      .as("a")
      .join(movieframe.as("b"), col("a.mvid") === col("b.mvid"))
      .show(false)

    println("-------------  为303推荐10部电影  -------------")
    model.recommendProducts(303, 10)
      .foreach(println)


    println("----------------  Sql实现方差&标准差  ------------------")
    val frame: DataFrame = model.predict(ratings.map(o => (o.user, o.product)).rdd)
      .toDF()
      .as("a")
      .join(ratings.as("b"), col("a.user") === col("b.user") && col("a.product") === col("b.product"))
      .withColumn("sqError^2", pow(col("a.rating") - col("b.rating"), lit(2.0)))

    frame.cache()

    frame.show()

    println("-----------------  均值&方差  -------------------")
    //   方差
    val MSE: Double = frame.groupBy().agg(mean("sqError^2").as("MSE")).collect().head(0).toString.toDouble

    //   标准差
    val RMSE: Double = math.sqrt(MSE)

    println(s"MSE = $MSE")
    println(s"RMSE = $RMSE")



    println("---------------  预测后30%数据中电影的的评分(取出前30条数据)  ----------------")
    val value: RDD[Rating] = model.predict(splitSet.last.map { case Rating(user, product, rating) => (user, product) }.rdd)
    value
      .take(30)
      .foreach(println)



    println("----------------  内置函数实现方差&标准差  -----------------")
    val metrics = new RegressionMetrics(frame.map { case Row(_, _, a, _, _, b, _) => (a.toString.toDouble, b.toString.toDouble) }.rdd)

    println(s"RegressionMetrics.MSE = ${metrics.meanSquaredError}")
    println(s"RegressionMetrics.RMSE = ${metrics.rootMeanSquaredError}")


    session.stop()
  }
}
