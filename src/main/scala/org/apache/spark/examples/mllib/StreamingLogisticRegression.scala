/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package org.apache.spark.examples.mllib

import org.apache.spark.SparkConf
import org.apache.spark.mllib.classification.StreamingLogisticRegressionWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Train a logistic regression model on one stream of data and make predictions
 * on another stream, where the data streams arrive as text files
 * into two different directories.
 *
 * The rows of the text files must be labeled data points in the form
 * `(y,[x1,x2,x3,...,xn])`
 * Where n is the number of features, y is a binary label, and
 * n must be the same for train and test.
 *
 * Usage: StreamingLogisticRegression <trainingDir> <testDir> <batchDuration> <numFeatures>
 *
 * To run on your local machine using the two directories `trainingDir` and `testDir`,
 * with updates every 5 seconds, and 2 features per data point, call:
 * $ bin/run-example mllib.StreamingLogisticRegression trainingDir testDir 5 2
 *
 * As you add text files to `trainingDir` the model will continuously update.
 * Anytime you add text files to `testDir`, you'll see predictions from the current model.
 *
 * 在一个数据流上训练逻辑回归模型并在另一个流上进行预测，其中数据流作为文本文件到达两个不同的目录。
 * 文本文件的行必须以(y,[x1,x2,x3,...,xn])形式标记数据点，其中 n 是特征数，y 是二进制标签，n 必须是训练和测试相同。
 * 用法：StreamingLogisticRegression        要使用两个目录 trainingDir和 testDir在本地计算机上运行，​​每 5 秒更新一次，每个数据点有 2 个特征，
 * 请调用： $ bin/run-example mllib.StreamingLogisticRegression trainingDir testDir 5 2 当您将文本文件添加到 trainingDir时模型会不断更新。每当您将文本文件添加到 testDir时，您都会看到来自当前模型的预测。
 */

/**
 * 流式逻辑回归
 */
object StreamingLogisticRegression {

  def main(args: Array[String]) {

    if (args.length != 4) {
      System.err.println(
        "Usage: StreamingLogisticRegression <trainingDir> <testDir> <batchDuration> <numFeatures>")
      System.exit(1)
    }

    val conf = new SparkConf().setMaster("local").setAppName("StreamingLogisticRegression")
    val ssc = new StreamingContext(conf, Seconds(args(2).toLong))

    val trainingData = ssc.textFileStream(args(0)).map(LabeledPoint.parse)
    val testData = ssc.textFileStream(args(1)).map(LabeledPoint.parse)

    val model = new StreamingLogisticRegressionWithSGD()
      .setInitialWeights(Vectors.zeros(args(3).toInt))

    model.trainOn(trainingData)
    model.predictOnValues(testData.map(lp => (lp.label, lp.features))).print()

    ssc.start()
    ssc.awaitTermination()

  }

}
// scalastyle:on println
