import java.io.File
import java.util

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.generic.CanBuildFrom
import test.ImplicitUitls._

import scala.collection.mutable
import scala.collection.parallel.immutable
/**
 * Hello world!
 *
 */

object App  {
  val log:Logger = Logger.getLogger(App.getClass)
  //{"Data.sku":"528471"}--评论数错 (历史数据）
  def main(args: Array[String]): Unit = {

    HBaseDao.getProduct("3921082")
    val maSet =Set("xx","dd")
    val myMap1 = mutable.Map("xx"->1,"dd"->1)
    val myMap2 = mutable.Map("xx"->1,"tt"->1)
}
}
