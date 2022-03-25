import org.apache.spark.sql.SparkSession

class HdfsWrite {
  def main(args: Array[String]): Unit = {
    val session: SparkSession = SparkSession.builder
      .master("local[*]")
      .appName(getClass.getName)
      //      .config("","")
      .getOrCreate()


    session.sparkContext.textFile("data/graphx/followers.txt").saveAsTextFile("hdfs://")

    session.stop()


  }
}
