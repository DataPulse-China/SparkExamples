package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SPARK_BUILD_USER, SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.jfree.chart.{ChartFactory, ChartFrame}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

object RatingTimes {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    val dataset = new DefaultCategoryDataset

    context.textFile("ml-100k/u.data")
      .map(o => {
        val f: Array[String] = o.split("\t")
        //   (用户id, 评级次数)
        (f(0).toInt, 1)
      })
      .reduceByKey(_ + _)
      .sample(withReplacement = false, 0.05 , System.currentTimeMillis())
      .collect()
      .toList
      .sortBy(_._1)
      .foreach(o => {
        println(o)
        dataset.setValue(o._2, "", o._1.toString)
      })

    val chart = ChartFactory.createBarChart(
      "评分情况", //标题
      "用户", //横坐标标签
      "评级次数", //纵坐标标签
      dataset,
      PlotOrientation.VERTICAL, //垂直
      true, //显示说明
      true, //显示工具提示
      false
    )

    val frame = new ChartFrame("评级次数", chart)
    frame.pack()
    frame.setVisible(true)



    context.stop()
  }
}
