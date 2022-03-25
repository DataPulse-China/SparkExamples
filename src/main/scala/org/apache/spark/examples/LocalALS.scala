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

/**
 * Alternating least squares matrix factorization.
 *
 * This is an example implementation for learning how to use Spark. For more conventional use,
 * please refer to org.apache.spark.ml.recommendation.ALS.
 *
 * 交替最小二乘矩阵分解。
 * 这是一个学习如何使用Spark的示例实现。对于更常规的用途，
 * 请参考org.apache.spark.ml.recommendation.ALS.
 */
object LocalALS {

  // Parameters set through command line arguments
  var M = 0 // 电影数量
  var U = 0 // 用户数量
  var F = 0 // 特征数量
  var ITERATIONS = 0 // 迭代次数
  val LAMBDA = 0.01 // 正则项系数

  def generateR(): RealMatrix = {
    val mh: RealMatrix = randomMatrix(M, F)
    //    val uh = randomMatrix(U, F)
    val uh: RealMatrix = randomMatrix(F, U)
    // mh矩阵 乘以 uh转置后的矩阵
    //    mh.multiply(uh.transpose())
    mh.multiply(uh)
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

  def updateMovie(i: Int, m: RealVector, us: Array[RealVector], R: RealMatrix): RealVector = {
    var XtX: RealMatrix = new Array2DRowRealMatrix(F, F)
    var Xty: RealVector = new ArrayRealVector(F)
    // For each user that rated the movie
    for (j <- 0 until U) {
      val u: RealVector = us(j)
      // Add u * u^t to XtX
      XtX = XtX.add(u.outerProduct(u))
      // Add u * rating to Xty
      Xty = Xty.add(u.mapMultiply(R.getEntry(i, j)))
    }
    // Add regularization coefficients to diagonal terms
    for (d <- 0 until F) {
      XtX.addToEntry(d, d, LAMBDA * U)
    }
    // Solve it with Cholesky
    new CholeskyDecomposition(XtX).getSolver.solve(Xty)
  }

  def updateUser(j: Int, u: RealVector, ms: Array[RealVector], R: RealMatrix): RealVector = {
    var XtX: RealMatrix = new Array2DRowRealMatrix(F, F)
    var Xty: RealVector = new ArrayRealVector(F)
    // For each movie that the user rated
    for (i <- 0 until M) {
      val m: RealVector = ms(i)
      // Add m * m^t to XtX
      XtX = XtX.add(m.outerProduct(m))
      // Add m * rating to Xty
      Xty = Xty.add(m.mapMultiply(R.getEntry(i, j)))
    }
    // Add regularization coefficients to diagonal terms
    for (d <- 0 until F) {
      XtX.addToEntry(d, d, LAMBDA * M)
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
    /*
    警告：这是ALS的一个简单实现，并作为一个例子给出！
请使用org.apache.spark.ml.recommendation.ALS
用于更常规的用途。
     */
  }

  def main(args: Array[String]): Unit = {

    args match {
      case Array(m, u, f, iters) =>
        M = m.toInt
        U = u.toInt
        F = f.toInt
        ITERATIONS = iters.toInt
      case _ =>
        System.err.println("Usage: LocalALS <电影数量> <用户数量> <特征数量> <迭代次数>")
        System.exit(1)
    }

    //    打印提示信息
    showWarning()

    //    println(s"Running with M=$M, U=$U, F=$F, iters=$ITERATIONS")
    println(s"Running with 电影数量=$M, 用户数量=$U, 特征数量=$F, 迭代次数=$ITERATIONS")

    // 获取一个
    val R: RealMatrix = generateR()

    // Initialize m and u randomly
    var ms: Array[RealVector] = Array.fill(M)(randomVector(F))
    var us: Array[RealVector] = Array.fill(U)(randomVector(F))

    // Iteratively update movies then users
    for (iter <- 1 to ITERATIONS) {
      println(s"Iteration $iter:")
      ms = (0 until M).map(i => updateMovie(i, ms(i), us, R)).toArray
      us = (0 until U).map(j => updateUser(j, us(j), ms, R)).toArray
      println("RMSE = " + rmse(R, ms, us))
      println()
    }
  }

  private def randomVector(n: Int): RealVector =
    new ArrayRealVector(Array.fill(n)(math.random))

  /**
   * 创建一个长rows宽cols的2D矩阵
   *
   * @param rows 行
   * @param cols 列
   * @return Array2DRowRealMatrix
   */
  private def randomMatrix(rows: Int, cols: Int): RealMatrix =
    new Array2DRowRealMatrix(Array.fill(rows, cols)(math.random))

}
// scalastyle:on println
