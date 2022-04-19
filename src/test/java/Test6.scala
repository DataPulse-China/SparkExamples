import scala.util.matching.Regex

object Test6 {
  def main(args: Array[String]): Unit = {
    val exampleApacheLogs = List(
      """10.10.10.10 - "FRED" [18/Jan/2013:17:56:07 +1100] "GET http://images.com/2013/Generic.jpg
        | HTTP/1.1" 304 315 "http://referall.com/" "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;
        | GTB7.4; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648; .NET CLR
        | 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR
        | 3.5.30729; Release=ARP)" "UD-1" - "image/jpeg" "whatever" 0.350 "-" - "" 265 923 934 ""
        | 62.24.11.25 images.com 1358492167 - Whatup""".stripMargin.lines.mkString,
      """10.10.10.10 - "FRED" [18/Jan/2013:18:02:37 +1100] "GET http://images.com/2013/Generic.jpg
        | HTTP/1.1" 304 306 "http:/referall.com" "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;
        | GTB7.4; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.04506.648; .NET CLR
        | 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR
        | 3.5.30729; Release=ARP)" "UD-1" - "image/jpeg" "whatever" 0.352 "-" - "" 256 977 988 ""
        | 0 73.23.2.15 images.com 1358492557 - Whatup""".stripMargin.lines.mkString
    )

    val r: Regex = "^([\\d.]+) (\\S+) (\\S+)".r

    def findIp(line: String) = {
      r.findFirstIn(line) match {
        case Some(r(ip, _, s)) => (ip,s)
        case _ => null
      }
    }

    exampleApacheLogs.map(line => findIp(line)).foreach(println)
  }
}
