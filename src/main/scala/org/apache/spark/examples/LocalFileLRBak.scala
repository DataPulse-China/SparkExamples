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
package org.apache.spark.examples

import breeze.linalg.{DenseVector, Vector}

import java.util.Random
import scala.io.BufferedSource

/**
 * Logistic regression based classification.
 *
 * This is an example implementation for learning how to use Spark. For more conventional use,
 * please refer to org.apache.spark.ml.classification.LogisticRegression.
 *
 * *基于逻辑回归的分类。
 *
 * 这是一个学习如何使用Spark的示例实现。对于更常规的用途，
 * 请参考org。阿帕奇。火花ml.分类。逻辑回归
 */
object LocalFileLRBak {
  val D = 10 // Number of dimensions 维数
  val rand = new Random(42) // 固定的随机因子\

  // 数据点
  case class DataPoint(x: Vector[Double], y: Double)

  // 解析文件的内容
  def parsePoint(line: String): DataPoint = {
    val nums: Array[Double] = line.split(' ').map(_.toDouble)
    DataPoint(new DenseVector(nums.slice(1, D + 1)), nums(0)) // slice 左开右闭获取数组片段
  }

  def showWarning(): Unit = {
    System.err.println(
      """WARN: This is a naive implementation of Logistic Regression and is given as an example!
        |Please use org.apache.spark.ml.classification.LogisticRegression
        |for more conventional use.
      """.stripMargin)
  }

  def main(args: Array[String]): Unit = {

    showWarning()
    val str: BufferedSource = scala.io.Source.fromFile(args(0))
    val lines: Array[String] = str.getLines().toArray
    str.close()
    val points: Array[DataPoint] = lines.map(parsePoint) // 积分
    val ITERATIONS: Int = args(1).toInt // 迭代次数

    // 将 w 初始化为随机值
    val w: DenseVector[Double] = DenseVector.fill(D) {
      2 * rand.nextDouble - 1
    }
    println("Initial w: " + w)

    for (i <- 1 to ITERATIONS) {
      println("On iteration " + i)
      /**
       * D 大小的 全 0.0 向量
       */
      val gradient: DenseVector[Double] = DenseVector.zeros[Double](D)
      for (p <- points) {
        // math.exp(x) 结果为: 欧拉数 e^x
        // x.dot(y) 结果为: x与y的点积
        val scale: Double = (1 / (1 + math.exp(-p.y * w.dot(p.x))) - 1) * p.y
        gradient += p.x * scale
      }
      w -= gradient
    }
    println("Final w: " + w)
  }
}
// scalastyle:on println
