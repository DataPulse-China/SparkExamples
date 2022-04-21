package sparklearn

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.jfree.data.general.DefaultPieDataset

import scala.collection.mutable
import scala.util.matching.Regex

/**
 * 电影标题的单词整合成了map
 */
object SimpleTextFeatures {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("test2")
    val context = new SparkContext(conf)

    val dataset = new DefaultPieDataset()

    val value: RDD[String] = context.textFile("ml-100k/u.item")

    def extract_title(str: String): String = {
      val r: Regex = """(\([1-9]{4}\))""".r
      r.findFirstIn(str) match {
        case Some(r(s)) => str.replace(s, "").trim
        case s => s.getOrElse("")
      }
    }

    val title_terms: RDD[Array[String]] = value
      .map(_.split("\\|")(1))
      .map(extract_title)
      .map(_.split(" "))

    //    titleWord
    //      .collect()
    //      .foreach(println)

    var index = 0
    val map: mutable.Map[String, Int] = mutable.Map()
    title_terms
      .flatMap(o => o)
      .distinct()
      .collect()
      .foreach(o => {
        map.put(o, index)
        index = index + 1
      })

    println(s"map.size = ${map.size}")



    //      .foreach(o =>{
    //        println(o)
    //        println(extract_title(o))
    //        println()
    //      })

    context.stop()

  }
}
