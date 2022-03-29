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

import breeze.linalg.{DenseVector, Vector, squaredDistance}

import java.util.Random
import scala.collection.mutable

/**
 * K-means clustering.
 *
 * This is an example implementation for learning how to use Spark. For more conventional use,
 * please refer to org.apache.spark.ml.clustering.KMeans.
 * K 均值聚类。
 * 这是学习如何使用 Spark 的示例实现。更常规的使用请参考 org.apache.spark.ml.clustering.KMeans。
 */
object LocalKMeansBak {
  val N = 1000
  val R = 1000 // Scaling factor 比例因子
  val D = 10
  val K = 10
  val convergeDist = 0.001
  val rand = new Random(42) //固定的随机数种子 保证每次取到的随机数都一样

  /**
   *
   * @return 返回了一个N长度,密集向量大小d 填充了随机数 的密集向量数组
   */
  def generateData: Array[DenseVector[Double]] = {
    /**
     * 返回一个 D 大小的密集向量 填充 随机双精度小数 乘以 R 的值
     *
     * @param i .
     * @return 返回一个密集向量的数组
     */
    def generatePoint(i: Int): DenseVector[Double] = {
      // DenseVector.fill(size)(value),在给定大小的向量中填充指定的值
      DenseVector.fill(D) {
        rand.nextDouble * R // 固定随机因子的随机小数 * R
      }
    }
    // 返回从这个数开始 填充 一直到 0 的值
    Array.tabulate(N)(generatePoint)
  }

  /**
   * 求出最近向量的下标
   *
   * @param p       密集向量数组中索引下标的元素
   * @param centers K点
   * @return
   */
  def closestPoint(p: Vector[Double], centers: mutable.HashMap[Int, Vector[Double]]): Int = {
    //    var index = 0
    var bestIndex = 0
    // 获取double的正无穷大的
    var closest: Double = Double.PositiveInfinity
    for (i <- 1 to centers.size) {
      // 取出 centers中 第 i 位的值
      val vCurr: Vector[Double] = centers(i)
      // 计算向量p到向量vCurr的平方距离
      val tempDist: Double = squaredDistance(p, vCurr)
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }
    // 返回最小值的坐标
    bestIndex
  }

  /**
   * 显示提示信息
   */
  def showWarning(): Unit = {
    System.err.println(
      """WARN: This is a naive implementation of KMeans Clustering and is given as an example!
        |Please use org.apache.spark.ml.clustering.KMeans
        |for more conventional use.
      """.stripMargin)
  }

  def main(args: Array[String]): Unit = {
    showWarning()
    val data: Array[DenseVector[Double]] = generateData //密集向量数组
    val points = new mutable.HashSet[Vector[Double]] // 积分
    val kPoints = new mutable.HashMap[Int, Vector[Double]] // K点
    var tempDist = 1.0

    /**
     * 如果积分的大小小于 Ｋ　就向 积分中填充 小于N的随机整数
     */
    while (points.size < K) {
      points.add(data(rand.nextInt(N)))
    }
    // 获取积分的遍历器
    val iter: Iterator[Vector[Double]] = points.iterator
    // 把所有的积分填充到K点中
    for (i <- 1 to points.size) {
      kPoints.put(i, iter.next())
    }
    // 打印初始化信息
    println("Initial centers: " + kPoints)

    // 温度去大于 > 收敛距离
    while (tempDist > convergeDist) {
      // 最近的 把密集向量数组的每一个映射成 (p -> k 最近的下标, (本向量, 1) ) 元组
      val closest: Array[(Int, (DenseVector[Double], Int))] = data.map(p => (closestPoint(p, kPoints), (p, 1)))
      // 根据最近的下标进行分组
      val mappings: Map[Int, Array[(Int, (DenseVector[Double], Int))]] = closest.groupBy[Int](x => x._1)
      // 把分组后结果中的值加起来
      val pointStats: Map[Int, (Vector[Double], Int)] = mappings.map { pair =>
        pair._2.reduceLeft[(Int, (Vector[Double], Int))] {
          case ((id1, (p1, c1)), (_, (p2, c2))) => (id1, (p1 + p2, c1 + c2))
        }
      }
      val newPoints: Map[Int, Vector[Double]] = pointStats.map { mapping => {
//        (最近的下标, 向量的和 * (1.0 / 向量的个数))
        (mapping._1, mapping._2._1 * (1.0 / mapping._2._2))
      }
      }

      tempDist = 0.0
      // 计算所有平方距离的和
      for (mapping <- newPoints) {
        tempDist += squaredDistance(kPoints(mapping._1), mapping._2)
      }

      // 插入所有的的值到kPoints
      for (newP <- newPoints) {
        kPoints.put(newP._1, newP._2)
      }
    }
    println("Final centers: " + kPoints)
  }
}
// scalastyle:on println
