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

/**
 * Logistic regression based classification.
 *
 * This is an example implementation for learning how to use Spark. For more conventional use,
 * please refer to org.apache.spark.ml.classification.LogisticRegression.
 *
 * 基于逻辑回归的分类。
 *
 *这是一个学习如何使用Spark的示例实现。对于更常规的用途，
 *请参考org。阿帕奇。火花ml.分类。逻辑回归。
 */
object LocalLRBak {
  val N = 10000  // 数据点数
  val D = 10   // 维数
  val R = 0.7  // 比例因子
  val ITERATIONS = 5 //迭代次数
  val rand = new Random(42) // 固定的随机因子

  case class DataPoint(x: Vector[Double], y: Double)

  def generateData: Array[DataPoint] = {
    def generatePoint(i: Int): DataPoint = {
      // 整数返回 -1 奇数返回 1
      val y: Int = if (i % 2 == 0) -1 else 1
      // D大小的随机密集向量
      val x: DenseVector[Double] = DenseVector.fill(D) {rand.nextGaussian + y * R}
      DataPoint(x, y)
    }
    // 返回 N 大小的DataPoint数组
    Array.tabulate(N)(generatePoint)
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
    val data: Array[DataPoint] = generateData
    // 将 w 初始化为随机值
    val w: DenseVector[Double] = DenseVector.fill(D) {
      2 * rand.nextDouble - 1
    }
    println("Initial w: " + w)

    for (i <- 1 to ITERATIONS) {
      println("迭代时: " + i)
      // 初始化为D大小的全0向量
      val gradient: DenseVector[Double] = DenseVector.zeros[Double](D)
      for (p <- data) {
        val scale: Double = (1 / (1 + math.exp(-p.y * w.dot(p.x))) - 1) * p.y
        gradient +=  p.x * scale
      }
      w -= gradient
    }

    println("Final w: " + w)
  }
}
// scalastyle:on println
