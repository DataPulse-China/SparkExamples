package org.apache.spark.examples.mllib

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.ElementwiseProduct
import org.apache.spark.mllib.linalg.Vectors

object ElementwiseProductExampleBak {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ElementwiseProductExample")
    val sc = new SparkContext(conf)
    val data = sc.parallelize(Array(Vectors.dense(1.0, 2.0, 3.0), Vectors.dense(4.0, 5.0, 6.0)))
    val transformingVector = Vectors.dense(0.0, 1.0, 2.0)
    val transformer = new ElementwiseProduct(transformingVector)
    val transformedData = transformer.transform(data)
    val transformedData2 = data.map(x => transformer.transform(x))
    println("transformedData: ")
    transformedData.foreach(x => println(x))
    println("transformedData2: ")
    transformedData2.foreach(x => println(x))
    sc.stop()
  }
}