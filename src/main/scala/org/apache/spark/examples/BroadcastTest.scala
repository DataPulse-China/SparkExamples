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

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
 * 广播变量测试
 * Usage: BroadcastTest [slices] [numElem] [blockSize]
 */
object BroadcastTest {
  def main(args: Array[String]): Unit = {

    val blockSize: String = if (args.length > 2) args(2) else "4096"

    val spark: SparkSession = SparkSession
      .builder()
      .appName("Broadcast Test")
      .master("local[*]")
      .config("spark.broadcast.blockSize", blockSize)
      .getOrCreate()

    // 获取 context实例
    val sc: SparkContext = spark.sparkContext
    // 获取参数 [slices]
    val slices: Int = if (args.length > 0) args(0).toInt else 2
    // 获取参数 [num]
    val num: Int = if (args.length > 1) args(1).toInt else 1000000
    // 获取一个 [0,num) 的数组 这里是 1000000
    val arr1: Array[Int] = (0 until num).toArray

    for (i <- 0 until 3) {
      println("Iteration " + i)
      println("===========")
      // 获取当前的纳秒时间戳
      val startTime: Long = System.nanoTime
      // 设置广播变量
      val barr1: Broadcast[Array[Int]] = sc.broadcast(arr1)
      // 获取是个长度的并行流 映射为广播变量的length
      val observedSizes: RDD[Int] = sc.parallelize(1 to 10, slices).map(_ => barr1.value.length)
      // Collect the small RDD so we can print the observed sizes locally.
      // 收集打印结果
      observedSizes.collect().foreach(i => println(i))
      println("Iteration %d took %.0f milliseconds".format(i, (System.nanoTime - startTime) / 1E6))
    }

    spark.stop()
  }
}
// scalastyle:on println
