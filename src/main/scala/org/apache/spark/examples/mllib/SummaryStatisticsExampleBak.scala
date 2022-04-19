package org.apache.spark.examples.mllib

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}

object SummaryStatisticsExampleBak {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SummaryStatisticsExample")
    val sc = new SparkContext(conf)
    val observations = sc.parallelize(
      Seq(
        Vectors.dense(1.0, 10.0, 100.0),
        Vectors.dense(2.0, 20.0, 200.0),
        Vectors.dense(3.0, 30.0, 300.0)
      )
    )
    val summary: MultivariateStatisticalSummary = Statistics.colStats(observations)
    println(summary.mean)
    println(summary.variance)
    println(summary.numNonzeros)
    sc.stop()
  }
}