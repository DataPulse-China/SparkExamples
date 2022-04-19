import org.apache.commons.math.linear.{Array2DRowRealMatrix, ArrayRealVector}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object Test7 {
  case class gu(custkey: Int, goods: Set[Int])

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.WARN)
    val name: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getName)
    val context = new SparkContext(name)

    context.parallelize(Array(1,2,3,4,5)).foreach(println)

    context.stop()
  }
}
