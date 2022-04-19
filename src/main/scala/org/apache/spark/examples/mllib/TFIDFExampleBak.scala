package org.apache.spark.examples.mllib

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

object TFIDFExampleBak {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("TFIDFExample")
    val sc = new SparkContext(conf)
    val documents: RDD[Seq[String]] = sc.textFile("data/mllib/kmeans_data.txt")
      .map(_.split(" ").toSeq)
    val hashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)
    //    tf.cache()
    //    val idf = new IDF().fit(tf)
    //    val tfidf: RDD[Vector] = idf.transform(tf)
    //    println("tfidf: ")
    //    tfidf.foreach(x => println(x))
    val idfIgnore = new IDF(minDocFreq = 2).fit(tf)
    val tfidfIgnore: RDD[Vector] = idfIgnore.transform(tf)
    println("tfidfIgnore: ")
    tfidfIgnore.foreach(x => println(x))
    sc.stop()
  }
}

