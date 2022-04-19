import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object Test9 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setAppName("TagsNew")
      .setMaster("local")
    val sc = new SparkContext(conf)
    val vector1: linalg.Vector = Vectors.dense(1, 2, 3, 4)
    val vector2: linalg.Vector = Vectors.dense(1, 2, 3, 4)
    val value: RDD[linalg.Vector] = sc.parallelize(Array(vector1, vector2))
    val matrix = new RowMatrix(value)

    sc.stop()
  }
}
