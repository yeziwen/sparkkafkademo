package test.hbase

import java.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.{Result, Scan}
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.sql.SparkSession



/**
  * Created by ye on 2017/2/22.
  */


object SparkReadHbase2 {

  val 京东_评论接口 ="699ba5d4-beea-435f-9fec-0dabd04bdc57"
  val 京东_商品详情 ="3b8e05ce-ca87-491c-8b89-9d2f25be511e"
  val 京东_推荐配件接口 ="a54ee6b1-498c-40fc-b811-4d75a39e94ab"
  val 京东_小商品列表页 ="68c3b283-5969-4e1c-afd9-6c0347a4315d"
  val 京东超市_搜索结果接口 ="c53b32b0-621a-4ea2-8118-b41164472156"
  val 京东超市_小商品列表页接口 ="0ddfffed-356d-45f2-abe4-4880d042fa16"
  val 京东超市_店铺列表接口 ="42e1c45f-fe09-4c17-abc4-3d8d9746269e"
  val 京东到家_店铺商品列表接口 ="2825cd61-457f-4115-8a0f-cd7b48257014"
  val 京东到家_店铺详情接口="ae990979-47d8-4b3e-9506-34358e927867"



  val spark = SparkSession.builder().master("local").appName("HBaseTest").getOrCreate()
  import spark.implicits._
  val hBaseConf = HBaseConfiguration.create()


  def printAllData(id:String):Unit ={
    val scan = new Scan()
    val filter = new SingleColumnValueFilter(Bytes.toBytes("content"), Bytes.toBytes("PageModelKey"),
      CompareOp.EQUAL, Bytes.toBytes(id)
    )
    scan.setFilter(filter)
    val proto = ProtobufUtil.toScan(scan)
    val ScanToString = Base64.encodeBytes(proto.toByteArray())
    hBaseConf.set(TableInputFormat.SCAN, ScanToString)
    hBaseConf.set(TableInputFormat.INPUT_TABLE, "jd-com")
    val primaryRDD=spark.sparkContext.newAPIHadoopRDD(hBaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
      .map { r =>
        Bytes.toString(r._2.getValue(Bytes.toBytes("content"), Bytes.toBytes("Data")))
      }
    primaryRDD.take(100).foreach(x =>
      println(x))
  }

  //打印所有条目
  def print条目(id:String):Unit = {
    val scan = new Scan()
    val filter = new SingleColumnValueFilter(Bytes.toBytes("content"), Bytes.toBytes("PageModelKey"),
      CompareOp.EQUAL, Bytes.toBytes(id)
    )
    scan.setFilter(filter)
    val proto = ProtobufUtil.toScan(scan)
    val ScanToString = Base64.encodeBytes(proto.toByteArray())
    hBaseConf.set(TableInputFormat.SCAN, ScanToString)
    hBaseConf.set(TableInputFormat.INPUT_TABLE, "jd-com")
    val primaryRDD=spark.sparkContext.newAPIHadoopRDD(hBaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
      .map { r =>
        Bytes.toString(r._2.getValue(Bytes.toBytes("content"), Bytes.toBytes("Data")))
      }
    println("sssssssssssssssssssssssssssssssssssssssssssss"+primaryRDD.count())
    val secondRDD= primaryRDD.map { r =>
      var map: java.util.HashMap[String, Object] = new Gson().fromJson(r,classOf[util.HashMap[String,Object]])
      var iterable: util.Iterator[java.util.Map.Entry[String, Object]] = map.entrySet().iterator()
      var result:Object = ""
      while (iterable.hasNext) {
        var entity: java.util.Map.Entry[String, Object] = iterable.next()
        var key = entity.getKey
        if("类目".equals(key)){
          result = entity.getValue
        }
        //怎么没有break;
      }
      result
    }.distinct().foreach(x=>println(x))
  }

//打印名字
  def printNameFrom京东超市_小商品列表页接口():Unit ={
    val scan = new Scan()
    val filter = new SingleColumnValueFilter(Bytes.toBytes("content"), Bytes.toBytes("PageModelKey"),
      CompareOp.EQUAL, Bytes.toBytes(京东超市_小商品列表页接口)
    )
    scan.setFilter(filter)
    val proto = ProtobufUtil.toScan(scan)
    val ScanToString = Base64.encodeBytes(proto.toByteArray())
    hBaseConf.set(TableInputFormat.SCAN, ScanToString)
    hBaseConf.set(TableInputFormat.INPUT_TABLE, "jd-com")
    val primaryRDD=spark.sparkContext.newAPIHadoopRDD(hBaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
      .map { r =>
        Bytes.toString(r._2.getValue(Bytes.toBytes("content"), Bytes.toBytes("Data")))
      }
    println("sssssssssssssssssssssssssssssssssssssssssssss商品总数:"+primaryRDD.count())
    val secondRDD= primaryRDD.map { r =>
      var map: java.util.HashMap[String, Object] = new Gson().fromJson(r,classOf[util.HashMap[String,Object]])
      var iterable: util.Iterator[java.util.Map.Entry[String, Object]] = map.entrySet().iterator()
      var result:Object = ""
      while (iterable.hasNext) {
        var entity: java.util.Map.Entry[String, Object] = iterable.next()
        var key = entity.getKey
        if("类目".equals(key)){
          result = entity.getValue
        }
        //怎么没有break;
      }
      result
    }.distinct().foreach(x=>println(x))
  }



  def main(args: Array[String]): Unit = {

    /* println("************京东超市_小商品列表页接口******************")
     printAllData(京东超市_小商品列表页接口)*/

   // printAllData(京东超市_店铺列表接口)
   /* val secondRDD= primaryRDD.map { r =>
      var map: java.util.HashMap[String, String] = new Gson().fromJson(r, new TypeToken[java.util.HashMap[String, Object]]() {}.getType())
      var iterable: util.Iterator[java.util.Map.Entry[String, String]] = map.entrySet().iterator()
      var sb: StringBuilder = new StringBuilder()
      while (iterable.hasNext) {
        var entity: java.util.Map.Entry[String, String] = iterable.next()
        var key = entity.getKey
        var value = entity.getValue
        sb.append(key + ":" + value + "*******")
      }
      sb.toString()
    }*/
  }
}
