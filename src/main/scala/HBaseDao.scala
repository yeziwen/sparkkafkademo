import java.io.IOException
import java.util

import org.apache.hadoop.hbase.{Cell, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.{CompareFilter, FilterList, RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.examples.mllib
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

/**
  * Created by ye on 2017/3/20.
  */
class HBaseTable {

  var  connection = ConnectionFactory.createConnection(HBaseUtils.getConf())
  var tables:Map[String,Table] =Map()

  def getTable(tableName:String): Table ={

    var table:Table = null
        try {
          tables.get(tableName).get
        } catch {
          case ex:Exception => println(ex.getMessage)
        }

   if(table == null) {
          table =connection.getTable(TableName.valueOf(tableName))
          if(table!=null) {
            tables.+((tableName,table))
          }
   }

        return table
  }
}

object HBaseDao {
  val spark = mllib.SparkUtils.getSpark()
  import spark.implicits._

  val hBaseTable = new HBaseTable()

  def  scanTableBySparkAPI(tableName:String):RDD[Result] ={
    var hBaseRDD=
      mllib.SparkUtils.getSpark().sparkContext.newAPIHadoopRDD(
        HBaseUtils.getConf(tableName),
        classOf[TableInputFormat],
        classOf[ImmutableBytesWritable],
        classOf[org.apache.hadoop.hbase.client.Result])
        .map(_._2)
    return hBaseRDD
  }


//不建議使用
  def  scanTableBySparkAPI(tableName:String,regexString:String):RDD[Result]={
    var hBaseRDD=
      mllib.SparkUtils.getSpark().sparkContext.newAPIHadoopRDD(
        HBaseUtils.getConf(tableName,regexString),
        classOf[TableInputFormat],
        classOf[ImmutableBytesWritable],
        classOf[org.apache.hadoop.hbase.client.Result])
        .map(_._2)
//ImmutableBytesWritable和Result类都是不可序列化的，所以不能在包含这两个类的RDD上做Action操作
    return hBaseRDD
  }



  def scanTableByHBaseAPI(tableName:String,regexString:String,productHotSellID:String):RDD[Result]={
    val scan = new Scan
    val filterList = new FilterList
    val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(regexString)) // OK 筛
    scan.setFilter(filter)
    val table = hBaseTable.getTable(tableName)
    val scanner = table.getScanner(scan)

    var seq = Seq[Result]()
    var  list = List[Result]()
    var  arr = Array[Result]()
    try {
      val iterator = scanner.iterator
      while (iterator.hasNext) {
        val next:Result = iterator.next
        //  list.add(next)
        val productHotSellID_ = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes(HBaseProductDetailKey.productHotSellID)))
        if(productHotSellID.equals(productHotSellID_))
          seq.+:(next)
      }
    } catch {
      case ex:Exception => println(ex.getMessage)
    } finally {
      scanner.close()
    }
    return spark.sparkContext.parallelize(seq,1)
  }


  def getProduct(id:String) ={
    val get: Get = new Get(Bytes.toBytes(id))
    val table = hBaseTable.getTable(ConstantUtils.hBaseJDProductTable)
    val result = table.get(get)
    println(s"productID:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("productID"))))
    println(s"downloadTime:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes(HBaseProductDetailKey.downloadTime))))
    println(s"jdProductListDownloadTime:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes(HBaseProductDetailKey.jdProductListDownloadTime))))
    println(s"jdProductDetailDownloadTime:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes(HBaseProductDetailKey.jdProductDetailDownloadTime))))
    println(s"jdHotSellDownloadTime:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes(HBaseProductDetailKey.jdHotSellDownloadTime))))
    println(s"jdCommentDownloadTime:"+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes(HBaseProductDetailKey.jdCommentDownloadTime))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("productName"))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("fitPeople"))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("productPrice"))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("commentCount"))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("downloadTime"))))
    println("productHotSellID="+Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("productHotSellID"))))
    println(Bytes.toString(result.getValue(Bytes.toBytes("content"),Bytes.toBytes("salesEvents"))))
  }


  def getHotSell(id:String) = {
    val scan = new Scan()
    val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(id)) // OK 筛
    scan.setFilter(filter)
    val table = hBaseTable.getTable(ConstantUtils.hBaseJDCommentTable)
    val scanner = table.getScanner(scan)
    try {
      val iterator = scanner.iterator
      while (iterator.hasNext) {
        val next:Result = iterator.next
        val rowKey = Bytes.toString(next.getRow())
        val productHotSellID = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes(HBaseProductDetailKey.productHotSellID)))
        val productName = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("productName")))
        val productID = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("productID")))
        val downloadTime = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("downloadTime")))
        println("rowKey="+rowKey+"name="+productName+"hotsellID="+productHotSellID+"productID="+productID+"downtime:"+downloadTime)
      }
    } catch {
      case ex:Exception => println(ex.getMessage)
    } finally {
      scanner.close()
    }





  }

  def getComment(id:String)  ={
    val scan = new Scan()
    val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(id)) // OK 筛
    scan.setFilter(filter)
    val table = hBaseTable.getTable(ConstantUtils.hBaseJDCommentTable)
    val scanner = table.getScanner(scan)
    try {
      val iterator = scanner.iterator
      while (iterator.hasNext) {
        val next:Result = iterator.next
        val rowKey = Bytes.toString(next.getRow())
        val productName = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("productName")))
        val productID = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("productID")))
        val downloadTime = Bytes.toString(next.getValue(Bytes.toBytes("content"), Bytes.toBytes("downloadTime")))
        println("rowKey="+rowKey+"name="+productName+"com="+"productID="+productID+"downtime:"+downloadTime)
      }
    } catch {
      case ex:Exception => println(ex.getMessage)
    } finally {
      scanner.close()
    }
  }





}
