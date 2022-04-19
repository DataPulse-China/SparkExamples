package org.apache.spark.examples.mllib

import org.apache.spark.mllib.random.RandomRDDs
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RandomRDDGenerationBak {

  def main(args: Array[String]) {
    val conf = new SparkConf().setMaster("local[*]").setAppName(s"RandomRDDGeneration")
    val sc = new SparkContext(conf)
    val normalRDD: RDD[Double] = RandomRDDs.normalRDD(sc, 1000000)
    println(s"Generated RDD of ${normalRDD.count()}" +
      " examples sampled from the standard normal distribution")
    println("  First 5 samples:")
    normalRDD.map(_ * 2 + 1).take(5).foreach(x => println(s"    $x"))
    sc.stop()
  }
}
