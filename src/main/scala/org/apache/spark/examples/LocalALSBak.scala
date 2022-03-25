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
object LocalALSBak {

  // Parameters set through command line arguments
  /**
   * 电影数量
   */
  var M = 0
  /**
   * 用户数量
   */
  var U = 0
  /**
   * 特征数量
   */
  var F = 0
  /**
   * 迭代次数
   */
  var ITERATIONS = 0
  /**
   * 正则项系数
   */
  val LAMBDA = 0.01

  def main(args: Array[String]): Unit = {
    // 匹配输入参数 否则退出
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

    showWarning()
    //    println(s"Running with M=$M, U=$U, F=$F, iters=$ITERATIONS")
    println(s"Running with 电影数量=$M, 用户数量=$U, 特征数量=$F, 迭代次数=$ITERATIONS")

    /**
     * 两个特征矩阵的乘积
     */
    val R: RealMatrix = generateR()

    // Initialize m and u randomly
    // 初始化 长度为 M 的
    /**
     * 数组大小为M 向量大小为F的实向量数组
     */
    var ms: Array[RealVector] = randomVector(M, F)
    /**
     * 数组大小为 U 向量大小为F的实向量数组
     */
    var us: Array[RealVector] = randomVector(U, F)

    // Iteratively update movies then users
    // 迭代更新电影，然后更新用户
    // 迭代次数
    for (iter <- 1 to ITERATIONS) {
      println(s"Iteration $iter:")
      ms = (0 until M).map(i => updateMovie(i, ms(i), us, R)).toArray
      us = (0 until U).map(j => updateUser(j, us(j), ms, R)).toArray
      println("RMSE = " + rmse(R, ms, us))
      println()
    }
  }

  /**
   * 返回 矩阵1(电影数量,特征数量) 乘以 矩阵2(特征数量,用户数量)
   *
   * @return RealMatrix
   */
  def generateR(): RealMatrix = {
    val mh: RealMatrix = randomMatrix(M, F)
    //    val uh = randomMatrix(U, F)
    val uh: RealMatrix = randomMatrix(F, U)
    //    mh矩阵 乘以 uh转置后的矩阵
    //    mh.multiply(uh.transpose())
    //    矩阵相乘
    mh.multiply(uh)
  }

  /**
   *
   * @param targetR R
   * @param ms
   * @param us
   * @return
   */
  def rmse(targetR: RealMatrix, ms: Array[RealVector], us: Array[RealVector]): Double = {
    /**
     * 实例化一个长度为 M 宽度为 U 的零矩阵
     */
    val r = new Array2DRowRealMatrix(M, U)
    for (i <- 0 until M; j <- 0 until U) {
      // 求ms(i) 与 us(j) 的外积 设置到 r 指定的位置中
      r.setEntry(i, j, ms(i).dotProduct(us(j)))
    }
    /**
     *  求 r 与 R 的差
     */
    val diffs: RealMatrix = r.subtract(targetR)
    var sumSqs = 0.0
    // 获取diff中 所有元素二次幂的和 保存到sumSqs中
    for (i <- 0 until M; j <- 0 until U) {
      val diff: Double = diffs.getEntry(i, j)
      sumSqs += diff * diff
    }
    // 求出平方根:  sumSqs 除以方阵元素数量的结果开平方根
    math.sqrt(sumSqs / (M.toDouble * U.toDouble))
  }

  /**
   * @param i  向量的下标
   * @param m  下标为 i 的向量
   * @param us 向量数组us
   * @param R  电影特征矩阵与用户特征矩阵的乘积
   * @return 向量
   */
  def updateMovie(i: Int, m: RealVector, us: Array[RealVector], R: RealMatrix): RealVector = {
    /**
     * 创建 F x F 全 0 实矩阵
     */
    var XtX: RealMatrix = new Array2DRowRealMatrix(F, F)
    /**
     * 创建长度为 F 全 0 的向量
     */
    var Xty: RealVector = new ArrayRealVector(F)
    // For each user that rated the movie
    // 对于每一个给电影评分的用户
    // 遍历每一个用户
    for (j <- 0 until U) {
      /**
       * 每一个位用户的向量
       */
      val u: RealVector = us(j)
      // Add u * u^t to XtX
      // Xtx 与 u 的外积 矩阵求和
      XtX = XtX.add(u.outerProduct(u))
      // Add u * rating to Xty
      // Xty 与  (u与 R(i,j)的乘积) 向量求和
      Xty = Xty.add(u.mapMultiply(R.getEntry(i, j)))
    }
    // Add regularization coefficients to diagonal terms
    // 将正则化系数添加到对角项
    for (d <- 0 until F) {
      // addToEntry 添加指定的值到矩阵的指定坐标中
      // XtX 添加 正则系数与用户的乘积到 主对角线上
      XtX.addToEntry(d, d, LAMBDA * U)
    }

    // Solve it with Cholesky
    // 对XtX进行乔尔斯基分解, 得到的结果与Xty求解 LT x X = Xty 求 X
    new CholeskyDecomposition(XtX).getSolver.solve(Xty)
  }

  /**
   *
   * @param j 向量的下标
   * @param u 下标为j的向量
   * @param ms 向量数组ms
   * @param R 电影特征矩阵与用户特征矩阵的乘积
   * @return 向量
   */
  def updateUser(j: Int, u: RealVector, ms: Array[RealVector], R: RealMatrix): RealVector = {
    // 创建 F x F 二维实矩阵
    var XtX: RealMatrix = new Array2DRowRealMatrix(F, F)
    // 创建一个阵列实向量
    var Xty: RealVector = new ArrayRealVector(F)

    // For each movie that the user rated
    // 对于用户评分的每部电影
    for (i <- 0 until M) {
      val m: RealVector = ms(i)
      // Add m * m^t to XtX
      XtX = XtX.add(m.outerProduct(m))
      // Add m * rating to Xty
      Xty = Xty.add(m.mapMultiply(R.getEntry(i, j)))
    }
    // Add regularization coefficients to diagonal terms
    // 向对角项添加正则化系数
    for (d <- 0 until F) {
      XtX.addToEntry(d, d, LAMBDA * M)
    }
    // Solve it with Cholesky
    // 对XtX进行乔尔斯基分解, 得到的结果与Xty求解 LT x X = Xty 求 X
    new CholeskyDecomposition(XtX).getSolver.solve(Xty)
  }

  /**
   * 打印提示信息
   */
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


  /**
   * 创建一个宽度为 n 的矩阵
   *
   * @param n 矩阵的大小
   * @return RealVector
   */
  private def randomVector(m: Int, n: Int): Array[RealVector] = {
    // ArrayRealVector 向量数组
    Array.fill(m)(new ArrayRealVector(Array.fill(n)(math.random)))
  }

  /**
   * 创建一个长rows宽cols 填充随机数据的2D矩阵
   * @param rows 行
   * @param cols 列
   * @return Array2DRowRealMatrix
   */
  private def randomMatrix(rows: Int, cols: Int): RealMatrix = {
    // Array2DRowRealMatrix 实矩阵
    new Array2DRowRealMatrix(Array.fill(rows, cols)(math.random))
  }
}
// scalastyle:on println
