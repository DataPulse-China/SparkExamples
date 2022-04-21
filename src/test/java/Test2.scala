import breeze.linalg.Options.Zero
import org.apache.spark.ml.linalg.{Matrices, Matrix, SparseMatrix}
import breeze.linalg._


object Test2 {
  def main(args: Array[String]): Unit = {

    val matrix: Matrix = Matrices.sparse(3, 3, Array(1, 1, 1, 3), Array(1, 1, 1), Array(4, 5, 6))
    println(matrix)
    println("---------------------")
    println(matrix.asInstanceOf[SparseMatrix].toDense)

    println("################################")

    val matrix1: Matrix = Matrices.sparse(4, 4, Array(1, 1, 1, 1, 4), Array(1, 1, 1, 1), Array(4, 5, 6, 7))
    println(matrix1)
    println("---------------------")
    println(matrix1.asInstanceOf[SparseMatrix].toDense)

  }
}
