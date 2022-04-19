package org.apache.spark.examples.mllib

import org.apache.spark.{SparkConf, SparkContext}

object StratifiedSamplingExampleBak {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("StratifiedSamplingExample")
    val sc = new SparkContext(conf)
    val data = sc.parallelize(
      Seq((1, 'a'), (1, 'b'), (2, 'c'), (2, 'd'), (2, 'e'), (3, 'f')))
    val fractions = Map(1 -> 0.1, 2 -> 0.6, 3 -> 0.3)
    val approxSample = data.sampleByKey(withReplacement = false, fractions = fractions)
    val exactSample = data.sampleByKeyExact(withReplacement = false, fractions = fractions)
    println("approxSample size is " + approxSample.collect().size.toString)
    approxSample.collect().foreach(println)
    println("exactSample its size is " + exactSample.collect().size.toString)
    exactSample.collect().foreach(println)
    sc.stop()
  }
}

