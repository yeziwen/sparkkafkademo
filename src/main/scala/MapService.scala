import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark
import org.apache.spark.examples.mllib.SparkUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.util
import org.apache.spark.util.BoundedPriorityQueue

import scala.util.control.Breaks._

/**
  * Created by ye on 2017/3/20.
  */
object MapService {


  val spark = SparkUtils.getSpark()
  import spark.implicits._




  implicit  def ordering() :Ordering[JDProductModel] = new Ordering[JDProductModel] {
    override def compare(x: JDProductModel, y: JDProductModel): Int ={
      x.index.compareTo(y.index)
    }
  }
  //能否提取map函数

  def getJDHotSellRDD(hBaseRDD: RDD[Result]): RDD[JDHotSellModel] = {
    val HotShellRDD = hBaseRDD.map { x =>
      val platformID = 2
      val sparkID = ""
      val productHotID = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productHotSellID.getBytes))
      val productID = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.sku.getBytes))
      val productName = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productName.getBytes))
      val productSpec = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productSpec.getBytes))
      val productPrice = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productPrice.getBytes))
      val productURL = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productURL.getBytes))
      val downloadTime = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.downloadTime.getBytes))
      val productImage = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.imageURL.getBytes))

      /*case class MysqlhotsellModel(
                                    val platformID:Int, val sparkID:String, val productHotID: String, val productID: String,
                                    val productName: String, val productSpec: String, val productPrice: String,
                                    val productURL: String, val downloadTime: String, val productImage: String
                                  )*/

      JDHotSellModel(platformID, sparkID, productHotID, productID, productName, productSpec, productPrice,
        productURL, downloadTime, productImage)
    }
    return HotShellRDD
  }



  def main(args: Array[String]): Unit = {


    val arr= Array((1,"a"),(1,"b"),(1,"c"),(2,"a"),(2,"c"),(2,"b"))

    val initialScores = Array(("Fred", 88.0), ("Fred", 95.0), ("Fred", 91.0), ("Wilma", 93.0), ("Wilma", 95.0), ("Wilma", 98.0))
    val d1 = spark.sparkContext.parallelize(initialScores)
    type MVType = (Int, Double) //定义一个元组类型(科目计数器,分数)
    val d2=d1.combineByKey(
      score => (1, score),//用于将RDD[K,V]中的V转换成一个新的值 V => C C1=(1, score)
      (c1: MVType, newScore) => (c1._1 + 1, c1._2 + newScore),//输入参数为(C1,V)，输出为新的C2,
      (c1: MVType, c2: MVType) => (c1._1 + c2._1, c1._2 + c2._2)//合并组合器函数，用于将两个C2类型值合并成一个C3类型，输入参数为(C2,C2)，输出为新的C3
    )
    d2.foreach(r=>println("xxxxxxxxxxxx"+r))

      d2. map {
      case (name, (num, socre)) => (name, socre / num)
    }.collect
  }

















}

























