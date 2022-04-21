import breeze.linalg._
import breeze.numerics._


object Test4 {
  def main(args: Array[String]): Unit = {
    //  计算余弦相似度
    val va: DenseVector[Double] = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    val vb: DenseVector[Double] = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    println(va.dot(vb) / (norm(va) * norm(vb)))
  }
}
