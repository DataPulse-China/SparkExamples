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

import org.apache.commons.math3.linear._
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession

import scala.collection.immutable

/**
 * Alternating least squares matrix factorization.
 *
 * This is an example implementation for learning how to use Spark. For more conventional use,
 * please refer to org.apache.spark.ml.recommendation.ALS.
 *
 * 交替最小二乘矩阵分解。这是学习如何使用 Spark 的示例实现。有关更常规的使用，请参阅 org.apache.spark.ml.recommendation.ALS。
 */
object SparkALSBak {

  // Parameters set through command line arguments
  var M = 0 // Number of movies
  var U = 0 // Number of users
  var F = 0 // Number of features
  var ITERATIONS = 0
  val LAMBDA = 0.01 // 正则化系数

  /**
   * 返回 矩阵 M,F x F,U的结果
   * @return
   */
  def generateR(): RealMatrix = {
    val mh: RealMatrix = randomMatrix(M, F)
    val uh: RealMatrix = randomMatrix(U, F)
    mh.multiply(uh.transpose())
  }

  def rmse(targetR: RealMatrix, ms: Array[RealVector], us: Array[RealVector]): Double = {
    val r = new Array2DRowRealMatrix(M, U)
    for (i <- 0 until M; j <- 0 until U) {
      r.setEntry(i, j, ms(i).dotProduct(us(j)))
    }
    val diffs: RealMatrix = r.subtract(targetR)
    var sumSqs = 0.0
    for (i <- 0 until M; j <- 0 until U) {
      val diff: Double = diffs.getEntry(i, j)
      sumSqs += diff * diff
    }
    math.sqrt(sumSqs / (M.toDouble * U.toDouble))
  }

  /**
   *
   * @param i
   * @param m
   * @param us
   * @param R
   * @return
   */
  def update(i: Int, m: RealVector, us: Array[RealVector], R: RealMatrix): RealVector = {
    val U: Int = us.length
    val F: Int = us(0).getDimension
    // F x F 矩阵
    var XtX: RealMatrix = new Array2DRowRealMatrix(F, F)
    // F 向量
    var Xty: RealVector = new ArrayRealVector(F)
    // 对于给电影评分的每个用户
    for (j <- 0 until U) {
      val u: RealVector = us(j)
      // XtX 添加 u的外积
      XtX = XtX.add(u.outerProduct(u))
      // Add u * rating to Xty
      //
      Xty = Xty.add(u.mapMultiply(R.getEntry(i, j)))
    }
    // Add regularization coefs to diagonal terms
    for (d <- 0 until F) {
      XtX.addToEntry(d, d, LAMBDA * U)
    }
    // Solve it with Cholesky
    new CholeskyDecomposition(XtX).getSolver.solve(Xty)
  }

  def showWarning(): Unit = {
    System.err.println(
      """WARN: This is a naive implementation of ALS and is given as an example!
        |Please use org.apache.spark.ml.recommendation.ALS
        |for more conventional use.
      """.stripMargin)
  }

  def main(args: Array[String]): Unit = {

    // 切片数量
    var slices = 0

    val options: immutable.Seq[Option[String]] = (0 to 4).map(i => if (i < args.length) Some(args(i)) else None)
    // 匹配传入参数
    options.toArray match {
      case Array(m, u, f, iters, slices_) =>
        M = m.getOrElse("100").toInt
        U = u.getOrElse("500").toInt
        F = f.getOrElse("10").toInt
        ITERATIONS = iters.getOrElse("5").toInt
        slices = slices_.getOrElse("2").toInt
      case _ =>
        System.err.println("Usage: SparkALS [M] [U] [F] [iters] [slices]")
        System.exit(1)
    }
    // 打印提示信息
    showWarning()

    println(s"Running with M=$M, U=$U, F=$F, iters=$ITERATIONS")

    val spark: SparkSession = SparkSession
      .builder
      .master("local[*]")
      .appName("SparkALS")
      .getOrCreate()

    val sc: SparkContext = spark.sparkContext

    val R: RealMatrix = generateR()

    // Initialize m and u randomly
    var ms: Array[RealVector] = Array.fill(M)(randomVector(F)) // Array[RealVector(size=F)] (M)
    var us: Array[RealVector] = Array.fill(U)(randomVector(F)) // Array[RealVector(size=F)] (U)

    // 迭代更新电影然后用户
    // 设置广播变量
    val Rc: Broadcast[asl] = sc.broadcast(R)
    var msb: Broadcast[Array[RealVector]] = sc.broadcast(ms)
    var usb: Broadcast[Array[RealVector]] = sc.broadcast(us)

    for (iter <- 1 to ITERATIONS) {
      println(s"Iteration $iter:")

      ms = sc.parallelize(0 until M, slices)
        .map(i => update(i, msb.value(i), usb.value, Rc.value))
        .collect()
      // 更新广播变量 msb
      msb = sc.broadcast(ms) // Re-broadcast ms because it was updated
      us = sc.parallelize(0 until U, slices)
        .map(i => update(i, usb.value(i), msb.value, Rc.value.transpose()))
        .collect()
      // 更新广播变量 usb
      usb = sc.broadcast(us) // Re-broadcast us because it was updated
      println("RMSE = " + rmse(R, ms, us))
      println()
    }

    spark.stop()
  }

  /**
   *
   * @param n 向量的大小
   * @return 返回一个 n 大小的向量
   */
  private def randomVector(n: Int): RealVector =
    new ArrayRealVector(Array.fill(n)(math.random))

  /**
   *
   * @param rows 宽度
   * @param cols 长度
   * @return 返回一个宽度为 rows 长度为 cols 填充随机数的矩阵
   */
  private def randomMatrix(rows: Int, cols: Int): RealMatrix =
    new Array2DRowRealMatrix(Array.fill(rows, cols)(math.random))

}
// scalastyle:on println
