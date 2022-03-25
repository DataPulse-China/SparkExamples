object Test3 {
  def main(args: Array[String]): Unit = {
    for (i <- 0 until 10; j <- 0 until 10) {
//      r.setEntry(i, j, ms(i).dotProduct(us(j)))
      println(s"i=${i} j=$j")
    }
  }
}
