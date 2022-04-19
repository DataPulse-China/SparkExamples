package org.apache.spark.examples.mllib

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.util.MLUtils

object SVMWithSGDExampleBak {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    val conf = new SparkConf().setMaster("local[*]").setAppName("SVMWithSGDExample")
    val sc = new SparkContext(conf)
    val data = MLUtils.loadLibSVMFile(sc, "data/mllib/sample_libsvm_data.txt")
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations)
    model.clearThreshold()
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()
    println("Area under ROC = " + auROC)
    model.save(sc, "target/tmp/scalaSVMWithSGDModel")
    val sameModel = SVMModel.load(sc, "target/tmp/scalaSVMWithSGDModel")
    sc.stop()


    val svmAlg = new SVMWithSGD()
    svmAlg.optimizer
      .setNumIterations(200)
      .setRegParam(0.1)
      .setUpdater(new L1Updater)
    val modelL1: SVMModel = svmAlg.run(training)
  }
}