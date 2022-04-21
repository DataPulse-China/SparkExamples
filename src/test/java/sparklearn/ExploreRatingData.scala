package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.util.StatCounter
import org.apache.spark.{SparkConf, SparkContext}
import org.jfree.chart.{ChartFactory, ChartFrame}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.category.DefaultCategoryDataset

/**
 * 探索评级数据
 */
object ExploreRatingData {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    val value: RDD[Int] = context.textFile("ml-100k/u.data")
      .map(_.split("\t")(2).toInt)

    val counter: StatCounter = value.stats()

    println(s"counter.count = ${counter.count}\n    counter.mean = ${counter.mean}\n    counter.stdev = ${counter.stdev}\n    counter.max = ${counter.max}")
    val dataset = new DefaultCategoryDataset

    value.countByValue()
      .toList
      .sortBy(_._1)
      .foreach(o => {
        dataset.setValue(o._2, "", o._1)
      })

    val chart = ChartFactory.createBarChart(
      "评分情况", //标题
      "分数", //横坐标标签
      "人数", //纵坐标标签
      dataset,
      PlotOrientation.VERTICAL, //垂直
      true, //显示说明
      true, //显示工具提示
      false
    )
    val frame = new ChartFrame("评分情况", chart)
    frame.pack()
    frame.setVisible(true)

    context.stop()
  }

}
