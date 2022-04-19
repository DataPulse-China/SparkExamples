//import org.apache.log4j.{Level, Logger}
//import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
//import org.apache.spark.rdd.RDD
//import org.apache.spark.{SparkConf, SparkContext}
//
//import java.util.UUID
//import scala.collection.mutable
//import scala.collection.mutable.ArrayBuffer
//
//object Test8 {
//  def main(args: Array[String]): Unit = {
//    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
//    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)
//
//    /**
//     * 1)判断参数
//     */
//    if(args.length < 3){
//      println(
//        """
//          |tags.TagsNew need two parameter <logDataInputPath><firstProcessInputPath><outputPath>
//          |<logDataInputPath>:原始数据输入路径
//          |<firstProcessInputPath> :第一处理后的输出路径
//          |<outputPath>:第二次处理后的输出路径
//        """.stripMargin)
//      System.exit(0)
//    }
//    val Array(logDataInputPath,firstProcessInputPath,outputPath)=args
//    /**
//     * 2)程序入口
//     */
//    val conf = new SparkConf()
//      .setAppName("TagsNew")
//      .setMaster("local")
//    val sc = new SparkContext(conf)
//    /**
//     * 获取用户id ,(1,set("a","b")),(2,set("a","c"))图计算的顶点user
//     */
//    //产生唯一id常用方法，
//    // 1、全局累加器longAccumulator
//    //      val uuid = sc.longAccumulator("uuid")
//    // 2、对set集合使用求hashcode(有缺陷）
//    // 3、使用UUID方法
////    val logData: RDD[String] = sc.textFile(logDataInputPath)
////    val users: RDD[(Long, Set[String])] = logData.map(line => {
////      val log: Any = Logs.getLogs(line)
////      val uidSet = Utils.getUserIds(log)
////      val uuid = UUID.randomUUID()
////      (uuid, uidSet)
////    })
//    //模拟测试数据
//    val array = Array((UUID.randomUUID(), Set("a", "b","f")), (UUID.randomUUID(), Set("a", "e", "f")),
//      (UUID.randomUUID(), Set("e", "h")),(UUID.randomUUID(), Set("jj", "hh","dd")),
//      (UUID.randomUUID(), Set("bb", "cc", "dd")), (UUID.randomUUID(), Set("bb","dd", "hh")))
//    val user: RDD[(Long, Set[String])] = sc.parallelize(array)
//    /**
//     *将用户id转变为 (1,a)(1,b)(1,a)(1,c)的形式
//     */
//    val step1Cid = user.flatMap(line => {
//      line._2.map(id => {
//        (line._1, id)
//      })
//    })
//
//    //      .foreach(println(_))
//    /**
//     * 按照value进行合并形成(a,(1,2,3))（b,(1,2))
//     */
//    val step2ComB = step1Cid.map(tuple => (tuple._2, tuple._1.toString))
//      .reduceByKey((uuid1, uuid2) => uuid1.concat(",").concat(uuid2))
//
//    //      .foreach(println(_))
//    /**
//     * 形成图计算的边形式，作为follower
//     * 1 2
//     * 1 3
//     * 1 1
//     */
//    val foll = step2ComB.flatMap(line => {
//      val fields = line._2.split(",")
//      val buffer = new ArrayBuffer[(String, String)]()
//      if (fields.length == 1) {
//        buffer += Tuple2(fields(0), fields(0))
//      } else {
//        fields.map(id => {
//          buffer += Tuple2(fields(0), id)
//        })
//      }
//      buffer
//    })
//    //      .map(line=>line._1+" "+line._2)
//    //        .saveAsTextFile("输出路径")
//
//    //      .foreach(println(_))
//    /**
//     * 使用图计算，形成cc
//     * 1 1
//     * 2 1
//     * 3 1
//     */
//    val followerRDD: RDD[Edge[String]] =
//      foll.map(line=>Edge(line._1.toLong,line._2.toLong,""))
//    val userRDD: RDD[(Long, String)] = step1Cid
//    val graph: Graph[String, String] = Graph(userRDD,followerRDD)
//    val cc: VertexRDD[VertexId] = graph.connectedComponents().vertices
//    //    cc.foreach(println(_))
//    /**
//     * cc于user进行join,
//     * 1 1 (a,b)
//     * 2 1(a,e)
//     * 3 1(a,f)
//     */
//    val ucjoin: RDD[(VertexId, (Set[String], VertexId))] = user.join(cc)
//    val uuidName: RDD[(VertexId, (Set[String]))] = ucjoin.map {
//      case (uid, (uname, uuid)) => (uuid, uname)
//    }
//    //      .foreach(println(_))
//    /**
//     * 按照唯一id进行reducebykey ,得到结果 1 （a,b,e,f)
//     * 然后变为如下形式,方便后续使用
//     * (a,1)
//     * (b,1)
//     * (e,1)
//     */
//    val resultID: RDD[(VertexId, Set[String])] = uuidName.reduceByKey(_ ++ _)
//    val vv: RDD[(String, VertexId)] = resultID.flatMap(line => {
//      line._2.map(nameID => (nameID, line._1))
//    })
//    //      .foreach(println(_))
//    val resultNameId = vv
//    //      .foreach(println(_))
//
//    /**
//     * 读取上次标签统计结果
//     * ANDROIDIDSHA1:AQ+KIQEBHEXF6X988FFNL+CVOOP (ZP上海市,2)	(APP马上赚,2)
//     */
//    val firstProcess: RDD[String] = sc.textFile(firstProcessInputPath)
//    //模拟数据
//    val fplogdata = Array("a (tag1,2) (tag2,1) (tag3,2)","h (tag1,2) (tag2,1) (tag5,2)",
//      "b (tag1,2) (tag2,1) (tag3,2)","f (tag1,2) (tag2,1) (tag4,2)",
//      "hh (tag5,2) (tag2,1) (tag3,2)","bb (tag1,2) (tag6,1) (tag3,2)",
//      "dd (tag1,2) (tag5,1) (tag3,2)","cc (tag1,2) (tag7,1) (tag6,2)",
//      "jj (tag7,2) (tag2,1) (tag3,2)","e (tag1,2) (tag2,1) (tag3,2)")
//    val fpdata: RDD[String] = sc.parallelize(fplogdata)
//    val logdataFP = fpdata.map(line => {
//      val fields = line.split(" ")
//      val tags = fields.slice(1, fields.length)
//        .map(str=>{
//          val nstr=str.substring(1,str.length-1)
//          var map = Map[String,Int]()
//          val ops = nstr.split(",")
//          map+=(ops(0)->ops(1).toInt)
//          map
//        })
//      (fields(0), tags)
//    })
//
//    //      .foreach(line=>println(line._1.mkString+"\t"+line._2.mkString(",")))
//    /**
//     * 将处理结果与得到的id结果进行join，
//     * (a,(1,*****))
//     * (b,(1,*****))
//     * :RDD[(VertexId, List[(String, Int)])]
//     */
//    val reSec: RDD[(VertexId, Array[mutable.Map[String, Int]])] = logdataFP.join(resultNameId)
//      .map {
//        case (uuidname, (tags, uuid)) =>
//          (uuid, tags)
//      }
//    val reSS: RDD[(VertexId, mutable.Map[String, Int])] = reSec.flatMap(line => {
//      line._2.map(aa => (line._1, aa))
//    })
//    //    reSS.foreach(println(_))
//
//
//
//    //      .foreach(println(_))
//
//    /**
//     * 按照唯一id再次进行reducebykey，获得二次统计结果
//     */
//    val lastRE: RDD[(VertexId, mutable.Map[String, Int])] = reSS
//      .reduceByKey {
//        case (map1, map2) => {
//          //获取map2的key值
//          val map1keyset = map1.keySet
//          val map2keyset = map2.keySet
//          for (key2 <- map2keyset) {
//            if (map1keyset.contains(key2)) {
//              val map1v: Option[Int] = map1.get(key2)
//              val map2v = map2.get(key2)
//              val newvalue = map1v.getOrElse(0) + map2v.getOrElse(0)
//              //将新的值放回去
//              map1 += (key2 -> newvalue)
//            } else {
//              //如果不存在，放在map1中
//              map1 += (key2 -> map2.get(key2).getOrElse(0))
//            }
//          }
//          map1
//        }
//      }
//
//    //合并成功
//    //(1205064238,Map(tag1 -> 6, tag7 -> 3, tag3 -> 8, tag6 -> 3, tag2 -> 2, tag5 -> 3))
//    //(1916870413,Map(tag1 -> 10, tag3 -> 6, tag2 -> 5, tag5 -> 2, tag4 -> 2))
//    //      .foreach(println(_))
//    //      .reduceByKey{
//    //        case (list1,list2)=>{
//    //          list1++list2.groupBy(_._1)
//    //              .map{
//    //                case (tg,tgv)=>
//    //                  val  sum = tgv.map(_._2).sum
//    //                  (tg,sum)
//    //              }
//    //        }.toList
//    //      }
//    //      .foreach(println(_))
//    /**
//     * 输出统计结果
//     */
//    lastRE.saveAsTextFile(outputPath)
//  }
//
//}
