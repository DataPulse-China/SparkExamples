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

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import java.io.File
import scala.io.BufferedSource
import scala.io.Source._

/**
 * 文件系统的读写测试
 * Simple test for reading and writing to a distributed
 * file system.  This example does the following:
 *
 *   1. Reads local file
 *      2. Computes word count on local file
 *      3. Writes local file to a DFS
 *      4. Reads the file back from the DFS
 *      5. Computes word count on the file using Spark
 *      6. Compares the word count results
 *
 * 1.读取本地文件
 * 2.计算本地文件上的字数
 * 3.将本地文件写入DFS
 * 4.从DFS中读回文件
 * 5.使用Spark计算文件的字数
 * 6.比较字数计算结果
 */
object DFSReadWriteTest {

  private var localFilePath: File = new File(".")
  private var dfsDirPath: String = ""

  private val NPARAMS = 2

  private def readFile(filename: String): List[String] = {
    val filename1: BufferedSource = fromFile(filename)
    val lineIter: Iterator[String] = filename1.getLines()
    val lineList: List[String] = lineIter.toList
    filename1.close()
    lineList
  }

  private def printUsage(): Unit = {
    val usage: String = "DFS Read-Write Test\n" +
      "\n" +
      "Usage: localFile dfsDir\n" +
      "\n" +
      "localFile - (string) local file to use in test\n" +
      "dfsDir - (string) DFS directory for read/write tests\n"
    println(usage)
  }

  /**
   * 解析参数
   * @param args 参数
   */
  private def parseArgs(args: Array[String]): Unit = {
    if (args.length != NPARAMS) {
      printUsage()
      System.exit(1)
    }

    var i = 0

    // 获取本地文件路径
    localFilePath = new File(args(i))

    // 如果文件不存在
    if (!localFilePath.exists) {
      System.err.println("Given path (" + args(i) + ") does not exist.\n")
      printUsage()
      System.exit(1)
    }

    //  如果文件不是
    if (!localFilePath.isFile) {
      System.err.println("Given path (" + args(i) + ") is not a file.\n")
      printUsage()
      System.exit(1)
    }

    i += 1

    //  设置文件系统目录
    dfsDirPath = args(i)
  }

  /**
   * 统计单词个数
   * @param fileContents 文件行列表
   * @return int
   */
  def runLocalWordCount(fileContents: List[String]): Int = {
    fileContents.flatMap(_.split(" "))
      .flatMap(_.split("\t"))
      .filter(_.nonEmpty)
      .groupBy(w => w)
      .mapValues(_.size)
      .values
      .sum
  }

  def main(args: Array[String]): Unit = {
    //  解析参数
    parseArgs(args)

//    println("Performing local word count")
    println("执行本地字数统计")
    //  读取文件
    val fileContents: List[String] = readFile(localFilePath.toString)

    val localWordCount: Int = runLocalWordCount(fileContents)

    println("Creating SparkSession")
    val spark: SparkSession = SparkSession
      .builder
      .appName("DFS Read Write Test")
      .getOrCreate()

    println("Writing local file to DFS")
    val dfsFilename: String = dfsDirPath + "/dfs_read_write_test"
    val fileRDD: RDD[String] = spark.sparkContext.parallelize(fileContents)
    fileRDD.saveAsTextFile(dfsFilename)

    println("Reading file from DFS and running Word Count")
    val readFileRDD: RDD[String] = spark.sparkContext.textFile(dfsFilename)
    //  统计单词的数量
    val dfsWordCount: Long = readFileRDD
      .flatMap(_.split(" "))
      .flatMap(_.split("\t"))
      .filter(_.nonEmpty)
      .map(w => (w, 1))
      .countByKey()
      .values
      .sum
    spark.stop()

    // 判断两次统计的结果是否一致
    if (localWordCount == dfsWordCount) {
      println(s"Success! Local Word Count ($localWordCount) " +
        s"and DFS Word Count ($dfsWordCount) agree.")
    } else {
      println(s"Failure! Local Word Count ($localWordCount) " +
        s"and DFS Word Count ($dfsWordCount) disagree.")
    }

  }
}
// scalastyle:on println
