import breeze.linalg.{Vector, squaredDistance}

object Test3 {
  def main(args: Array[String]): Unit = {
    //    for (i <- 0 until 10; j <- 0 until 10) {
    //      r.setEntry(i, j, ms(i).dotProduct(us(j)))
    //      println(s"i=${i} j=$j")
    //    }
    //    Array.tabulate(1)(_ - 1).foreach(println)
    //    println(DenseVector.fill(10)(1))
    //    val infinity: Double = Double.PositiveInfinity
    //    println()
    //    val random = new Random(10)
    //    for(i <- 1 until(100)){
    //      println(random.nextInt())
    //    }

    //    val a = 1.0 / 0
    //    println(a)
    //    println(Vector(1, 2).apply(1))
    val ints: Vector[Double] = Vector(2.0, 2, 2, 2)
    val ints1: Vector[Double] = Vector(4.0, 4, 2, 9)
    val a: Double = squaredDistance(ints, ints1)
    println(a)
    val d: Double = math.pow(2 - 4, 2) + math.pow(2 - 4, 2) + math.pow(2 - 2, 2) + math.pow(2 - 9, 2)
    println(d)

    //    向量求两点之间的距离 a (x1,y1), b(x2,y2) , a到b的距离 (x1-x2)^2 + (y1-y2)^2

    //    println(math.pow(2, 3))


    //    val x1 = 1
    //    val x2 = 1
    //    val y1 = 3
    //    val y2 = 2
    //    val z1 = 2
    //    val z2 = 1
    //    val i: Int = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2)
    //    println(math.sqrt(i))

  }
}
