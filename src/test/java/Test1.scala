package learn

import breeze.linalg._
import breeze.linalg.eig.DenseEig
import breeze.linalg.svd.DenseSVD
import breeze.numerics._

object Test1 {
  def main(args: Array[String]): Unit = {
    /**
     * 短矩阵构造函数
     */
    //    创建全0矩阵
    val m1: DenseMatrix[Double] = DenseMatrix.zeros[Double](2, 3)
    //    创建全0向量
    val v1: DenseVector[Double] = DenseVector.zeros[Double](3)
    //    创建全1向量
    val v2: DenseVector[Double] = DenseVector.ones[Double](3)
    //    按数值填充向量
    val v3: DenseVector[Double] = DenseVector.fill[Double](3)(1.0)
    //    生成随机向量
    val v4: DenseVector[Int] = DenseVector.range(0, 10, 2)
    val v5: Vector[Int] = Vector.range(0, 10, 2)
    //    单位矩阵
    val m2: DenseMatrix[Double] = DenseMatrix.eye[Double](3)
    //    对角矩阵
    val m3: DenseMatrix[Int] = diag(DenseVector(1, 2, 3))
    //    按照行创建矩阵
    val m4: DenseMatrix[Double] = DenseMatrix((1.0, 2.0), (1.0, 2.0))
    //    按照行创建向量
    val v6: DenseVector[Double] = DenseVector(1.0, 2.0)
    //    向量的转置
    val t1: Transpose[DenseVector[Double]] = v6.t
    //    从函数创建向量
    val v7: DenseVector[Int] = DenseVector.tabulate(10)(_ * 2)

    /**
     * 获取矩阵子集函数
     */

    val a: DenseMatrix[Double] = DenseMatrix(
      (1.0, 2.0, 3.0),
      (4.0, 5.0, 6.0),
      (7.0, 8.0, 9.0)
    )

    val b: DenseMatrix[Double] = DenseMatrix.ones[Double](3, 3)

    val va: DenseVector[Double] = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    val vb: DenseVector[Double] = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    //    获取指定位置
    a(0, 1)
    //    获取向量子集
    va(1 to 2)
    //    按照指定步长获取子集
    va(5 to 0 by -1)
    //    按照开始位置到结束位置获取子集
    va(1 to -1)
    //    获取最后一个元素
    va(-1)
    //    获取矩阵的指定列
    a(::, 1)

    /**
     * 短矩阵元素访问及操作函数
     */

    //    调整矩阵的形状(行列乘积不变)
    a.reshape(3, 3)
    //    短矩阵转向量
    a.toDenseVector
    //    复制矩阵下三角 (矩阵对角线以下的部分,包括对角线)
    lowerTriangular(a)
    //    复制矩阵上三角 (矩阵对角线以上的部分,包括对角线)
    upperTriangular(a)
    //    矩阵复制
    a.copy
    //    取出矩阵对角线元素
    diag(a)
    //    向量子集赋数值
    va(0 to 2) := 5.0
    //    子集赋向量
    va(1 to 4) := DenseVector(1.0, 2.0, 3.0, 4.0)
    //    矩阵的赋值
    a(1.to(2), 1.to(2)) := 0.0
    //    矩阵列赋值
    a(::, 2) := 5.5
    //    垂直连接矩阵
    DenseMatrix.vertcat(a, a)
    //    横向连接矩阵
    DenseMatrix.horzcat(a, a)
    //    向量连接
    DenseVector.vertcat(va, va)

    /**
     * 矩阵数值计算函数
     */
    //    矩阵的加法
    a + b
    //    矩阵乘法
    a :* b
    //    矩阵除法
    a :/ b
    //    矩阵比较
    a :< b
    //    矩阵相等
    a :== b
    //    矩阵追加
    a :+= b
    //    矩阵追乘
    a :*= b
    //    向量的点积
    va.dot(vb)
    //    元素的最大值
    max(a)
    //    元素最大值的位置
    argmax(a)

    /**
     * 矩阵求和函数
     */
    //    元素求和
    sum(a)
    //    每一列求和
    sum(a(::, *))
    //    每一行求和
    sum(a(*, ::))
    //    对角线元素求和
    trace(a)
    //    向量的累加运算
    accumulate(va)

    /**
     * 矩阵布尔运算函数
     */
    //    任意元素非零
    any(a)
    //    所有元素非0
    all(a)

    /**
     * 矩阵线性代数计算函数
     */

    //    线性求解
    a / b
    //    转置
    a.t
    //    求行列式

    //    val value: DenseMatrix[Double] = DenseMatrix.horzcat(a, a)
    //    val d1: Double = value(0, 0) * value(1, 1) * value(2, 2)
    //    val d2: Double = value(0, 1) * value(1, 2) * value(2, 3)
    //    val d3: Double = value(0, 2) * value(1, 3) * value(2, 4)
    //
    //    val d4: Double = value(2, 0) * value(1, 1) * value(0, 2)
    //    val d5: Double = value(2, 1) * value(1, 2) * value(0, 3)
    //    val d6: Double = value(2, 2) * value(1, 3) * value(0, 4)

    //    println(d1 + d2 + d3 - d4 - d5 - d6)


    det(a)
    //    求逆
    /**
     * 判断矩阵是否可逆:
     * 行列式不等于0
     */
    inv(a)
    //    求伪逆
    pinv(a)
    //    求范数
    norm(vb)
    //    特征值和特征向量
    val c: DenseMatrix[Double] = DenseMatrix.ones[Double](3, 3)
    eigSym(c)
    //    特征值
    eig(c)
    //    奇异值分解
    svd(a)
    //    求矩阵的秩
    rank(a)
    //    矩阵的行数
    a.rows
    //    矩阵的列数
    a.cols
    /**
     * 矩阵取整函数
     */
    //    四舍五入
    round(a)
    //    最小整数
    ceil(a)
    //    最大整数
    floor(a)
    //    符号函数
    signum(a)
    //    绝对值
    abs(a)



  }
}
