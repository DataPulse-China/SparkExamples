package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.jfree.chart.{ChartFactory, ChartFrame, JFreeChart}
import org.jfree.data.general.DefaultPieDataset

/**
 * 年龄分布
 */
object Altersverteilung {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    val dataset = new DefaultPieDataset()

    val value: RDD[String] = context.textFile("ml-100k/u.user")
    value.map(_.split("\\|"))
      //      .foreach(println)
      .map(o => {
        val i: Int = o(1).toInt / 10
        (s"${i * 10}_${(i + 1) * 10}", 1)
      })
      .reduceByKey(_ + _)
      .collect()
      .foreach(o => {
        dataset.setValue(o._1, o._2)
      })

    val chart: JFreeChart = ChartFactory.createPieChart(
      "年龄分布",
      dataset,
      true,
      true,
      true
    )

    val frame = new ChartFrame("年龄分布", chart)
    frame.pack()
    frame.setVisible(true)


    context.stop()

  }
}
