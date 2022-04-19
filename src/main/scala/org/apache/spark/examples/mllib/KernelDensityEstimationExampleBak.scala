package org.apache.spark.examples.mllib

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.stat.KernelDensity
import org.apache.spark.rdd.RDD

object KernelDensityEstimationExampleBak {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("KernelDensityEstimationExample")
    val sc = new SparkContext(conf)
    val data: RDD[Double] = sc.parallelize(Seq(1, 1, 1, 2, 3, 4, 5, 5, 6, 7, 8, 9, 9))
    val kd = new KernelDensity()
      .setSample(data)
      .setBandwidth(3.0)
    val densities = kd.estimate(Array(-1.0, 2.0, 5.0))
    densities.foreach(println)
    sc.stop()
  }
}