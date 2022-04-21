package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{ChartFactory, ChartFrame, ChartPanel, JFreeChart}
import org.jfree.data.category.DefaultCategoryDataset

import scala.swing.Dimension

/**
 * 探索电影数据
 */
object ErkundenMovieData {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    def getYear(str: String): Int = {
      if (str.length == 0) return 1900
      str.substring(str.length - 4, str.length).toInt
    }

    val dataset = new DefaultCategoryDataset

    context.textFile("ml-100k/u.item")
      .map(_.split("\\|")(2))
      .map(getYear)
      .filter(_ > 1900)
      .map(1998 - _)
      .countByValue()
      .toList
      .sortBy(_._2)
      .foreach(o => {
        println(o)
        dataset.setValue(o._1, "", o._2)
      })

    val chart = ChartFactory.createBarChart(
      "电影年龄分布", //标题
      "电影年龄", //横坐标标签
      "数量", //纵坐标标签
      dataset,
      PlotOrientation.VERTICAL, //垂直
      true, //显示说明
      true, //显示工具提示
      false
    )

    val frame = new ChartFrame("年龄分布", chart)
    frame.pack()
    frame.setVisible(true)

    context.stop()
  }
}
