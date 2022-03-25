import org.apache.commons.math3.linear.{Array2DRowRealMatrix, ArrayRealVector, CholeskyDecomposition, RealMatrix, RealVector}

import scala.math.random


object Test2 {
  def main(args: Array[String]): Unit = {
    //  实例化一个 10 个大小随机填充的向量
    //    println(new ArrayRealVector(Array.fill(10)(math.random)))
    //  实例化一个 两个长度数组,填充三个长度的随机向量
    //    println(Array.fill(2)(new ArrayRealVector(Array.fill(3)(math.random))).toList)
    //    println()
    //  实例化一个四个长度的随机向量
    //    println(new ArrayRealVector(Array.fill(4)(math.random)))
    //    println()
    //  实例化一个两行三列的随机矩阵
    //    println(new Array2DRowRealMatrix(Array.fill(2, 3)(math.random)).getRowVector(1))
    //  读取文件测试
    //    fromFile("data/graphx/followers.txt").getLines().foreach(println)
    //  实例化十个 两个长度数组,填充四个长度的随机向量
    //    val vectors: Array[ArrayRealVector] = Array.fill(10)(new ArrayRealVector(Array.fill(4)(math.random)))
    //    (0 until 10 ).foreach(i =>{
    //      println(s"i = ${i}, vectors($i) = ${vectors(i)}")
    //    })

    // 实例化一个 10 x 10的矩阵
    //    println(new Array2DRowRealMatrix(10, 10))

    // 两个向量的外积 向量可以任意长度
    //    val vector: ArrayRealVector = new ArrayRealVector(Array(1.0, 2.0, 3.0))
    //    val vector2: ArrayRealVector = new ArrayRealVector(Array(2.0, 3.0))
    //    val matrix: RealMatrix = vector.outerProduct(vector2)
    //    println(matrix)
    //    var aa: RealMatrix = new Array2DRowRealMatrix(3, 3)
    //    aa = aa.add(matrix)
    //    println(aa)
    //    aa = aa.add(aa)
    //    println(aa)

    //  获取矩阵中指定位置的值
    //    val matrix = new Array2DRowRealMatrix(Array.fill(2, 2)(random))
    //    matrix.getData.map(_.toList).foreach(println)
    //    println(matrix.getEntry(0, 1))

    //  向量求和
    //        val vector = new ArrayRealVector(Array(1.0, 2.0, 3.0, 4.0))
    //        println(vector.add(vector))
    //        vector.mapMultiply(10)

    // 填充主对角线
    //    val matrix = new Array2DRowRealMatrix(10, 10)
    //    for(i<-0 until 10){
    //      matrix.addToEntry(i,i,10)
    //    }
    //    matrix.getData.map(_.toList).foreach(println)

    //    矩阵的乘法
    //    val matrix = new Array2DRowRealMatrix(Array(Array(1.0, 2.0, 3.0),Array(1.0, 2.0, 3.0),Array(1.0, 2.0, 3.0)))
    //    val matrix1: Array2DRowRealMatrix = matrix.multiply(matrix)
    //    matrix.getData.map(_.toList).foreach(println)
    //    println()
    //    matrix1.getData.map(_.toList).foreach(println)

    //    val matrix = new Array2DRowRealMatrix(Array(Array(1.0, 2, 3), Array(4.0, 5, 6), Array(7.0, 8, 9)))
    //    val matrix = new Array2DRowRealMatrix(Array.fill(10,9)(random*10))

    //    val vector = new ArrayRealVector(Array.fill(10)(random))

    /**
     * 把一个矩阵分解为一个矩阵乘以该矩阵矩阵的转置
     */

    //    val vector = new ArrayRealVector(Array(1.0, 1, 1))
    //    val matrix: RealMatrix = vector.outerProduct(vector)
    //    matrix.getData.map(_.toList).foreach(println)
    //    println("_____________________________________")
    //
    //    for (i <- 0 until 3) {
    //      matrix.addToEntry(i, i, i * 1)
    //    }
    //
    //    matrix.getData.map(_.toList).foreach(println)
    //    // 乔尔斯基分解
    //    val decomposition = new CholeskyDecomposition(matrix)
    //
    //    val t: RealMatrix = decomposition.getLT
    //    t.getData.map(_.toList).foreach(println)
    //    println("------------------------------------")
    //    val l: RealMatrix = decomposition.getL
    //    l.getData.map(_.toList).foreach(println)
    //    //    他们两个的乘积为原值
    //    println()
    //    l.multiply(t).getData.map(_.toList).foreach(println)
    //    println()
    //    t.multiply(l).getData.map(_.toList).foreach(println)
    //
    //
    //    println("-------------------------------------------")
    //    // 乔尔斯基分解后的结果 LT ,  求值 LT x X = B , 返回的结果为 X
    //    val LT: RealMatrix = decomposition.getLT
    //    val X: RealVector = decomposition.getSolver.solve(vector)
    //    val B: ArrayRealVector = vector
    //    LT.getData.map(_.toList).foreach(println)
    //    println()
    //    println("X")
    //    println()
    //    println(X)
    //    println()
    //    println("=")
    //    println()
    //    println(B)
    //    println()


    //  矩阵乘以向量
    //    val vector1 = new ArrayRealVector(Array(1.0, 2, 3))
    //    val matrix: RealMatrix = vector1.outerProduct(vector1)
    //    matrix.getData.map(_.toList).foreach(println)
    //    println()
    //    println(vector1)
    //    println()+
    //    println(matrix.preMultiply(vector1))



    val vector = new ArrayRealVector(Array(1.0, 2, 3))
    val d: Double = vector.dotProduct(vector)
    println(d)
  }
}
