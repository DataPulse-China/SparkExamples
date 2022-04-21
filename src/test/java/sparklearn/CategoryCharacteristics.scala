package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.jfree.data.general.DefaultPieDataset

import scala.collection.mutable

object CategoryCharacteristics {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    val dataset = new DefaultPieDataset()

    val value: RDD[String] = context.textFile("ml-100k/u.user")

    var index = 0;
    val map: mutable.Map[String, Int] = mutable.Map()
    value.map(_.split("\\|")(3))
      .distinct()
      .sortBy(o => o)
      .collect()
      .foreach(o => {
        map.put(o, index)
        index = index + 1
      })

    println(map.getOrElse("doctor", -1))

    context.stop()

  }
}
