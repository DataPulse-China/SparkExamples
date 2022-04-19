import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.linalg
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.rdd.RDD

object Test10 {
  def main(args: Array[String]): Unit = {
    //    val vector1: linalg.Vector = Vectors.sparse(5, Seq((1, 3.0), (2, 7.0))) // 向量大小 , ((索引,值),(索引,值)....)
    //    val vector2: linalg.Vector = Vectors.sparse(5, Array(1, 2), Array(3.0, 7.0)) // 向量大小 , ((索引1,索引2),(值1,值2)....)
    //    println(vector1.toDense)
    //    println(vector2.toDense)
    //
    //    // 创建一个稠密向量
    //    val vector: linalg.Vector = Vectors.dense(Array(0.0, 3.0, 7.0, 0.0, 0.0)) // (值1,值2...)
    //    // 转稀疏向量
    //    println(vector.toSparse)

    Logger.getLogger("org").setLevel(Level.WARN)
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName(getClass.getSimpleName)
    val context = new SparkContext(conf)

    val data = Array(
      Vectors.sparse(5, Seq((1, 1.0), (3, 7.0))),
      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0))

    val vectorRdd: RDD[linalg.Vector] = context.parallelize(data)
    val matrix: RowMatrix = new RowMatrix(vectorRdd)

    matrix.rows.foreach(println)

    context.stop()

  }
}
