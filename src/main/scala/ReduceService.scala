import model.JDProductModel
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.examples.mllib.SparkUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable


/**
  * Created by ye on 2017/3/20.
  */

object ReduceService {
  /*  rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category1)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))
     */
  //.toDF().write.mode("append").jdbc("jdbc:mysql://192.168.10.86:3306/test", "product", MysqlUtils.getProperties())

  val spark = SparkUtils.getSpark()
  import spark.implicits._
  def getSpark:SparkSession ={
    spark
  }

  def MapFromResultToJDProduct(x:Result):JDProductModel = {
    val sparkID = ""
    val platformID = 2
    var index = -1 //表示該索引無效
    if (x.getValue("content".getBytes(), HBaseProductDetailKey.index.getBytes) != null) {
      val indexString = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.index.getBytes))
      index = Integer.parseInt(indexString)
    }
    var commentCount = 0
    if (x.getValue("content".getBytes(), HBaseProductDetailKey.commentCount.getBytes) != null) {
      val commentCountString = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.commentCount.getBytes))
      commentCount = Integer.parseInt(commentCountString)
    }
    val productID = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.sku.getBytes))
    val rowkey = Bytes.toString(x.getRow)
    val productName = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productName.getBytes))
    val productBrand = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productBrand.getBytes))
    val productSpec = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productSpec.getBytes))
    val productPrice = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productPrice.getBytes))
    val productArea = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productArea.getBytes))
    val productURL = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productURL.getBytes))
    val standardCategory = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.standardCategory.getBytes))
    val category = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.category.getBytes))
    val salesEvents = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.salesEvents.getBytes))
    val downloadTime = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.downloadTime.getBytes))
    val productHotSellID = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productHotSellID.getBytes))
    val hasFittingsRecommend = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.hasFittingsRecommend.getBytes))
    val productSalesTime = Bytes.toString(x.getValue("content".getBytes(), HBaseProductDetailKey.productSalesTime.getBytes))
    val userProvince = ""
    JDProductModel(index, productHotSellID,userProvince, commentCount, sparkID, platformID, productID,
      productName, productBrand, productSpec,
      productPrice, productArea, productSalesTime,
      standardCategory, category, salesEvents,
      productURL,
      hasFittingsRecommend, downloadTime)
  }

  def ReduceProduct:Array[JDProductModel]={

    val hBaseRDD:RDD[Result]= HBaseDao.scanTableBySparkAPI(ConstantUtils.hBaseJDProductTable)

    val productRDD = hBaseRDD.map(MapFromResultToJDProduct(_))

    val productRDD_filter1 =productRDD.filter(r=>r.index !=  -1 ).filter{r=>
        var flag = false
        if(r.downloadTime.compareTo(ConstantUtils.get3DayTimeString())>0)
        { flag =true}
      flag
    }   //过滤1：产品是3天内的

    val productRDD_filter2 =productRDD_filter1.filter{r=>
      var flag = false
      for(ele <- ConstantUtils.categoryArrays ){
        if(ele.equals(r.standardCategory))
        { flag =true}
      }
      flag
    }   //过滤2：产品是指定类别的

    productRDD_filter2.persist()//迭代任務

    var productArrayTemp:Array[JDProductModel] = null
    for(category <- ConstantUtils.categoryArrays){
      val temp = productRDD_filter2.filter(r=>r.standardCategory.equals(category)).sortBy(r=>r.index).take(100)
      temp.foreach(r=>println(r))
      if(productArrayTemp==null) productArrayTemp=temp else productArrayTemp ++= temp
    }
    productRDD_filter2.unpersist()
    productArrayTemp
  }

  val productArray:Array[JDProductModel] = ReduceProduct

  def MapFromResultToJDCommentModel(x:Result):JDCommentModel = {
      val rowKey = Bytes.toString(x.getRow)
      val commentID = Bytes.toString(x.getValue("content".getBytes(), HBaseCommentDetailKey.id.getBytes))
      val productID = Bytes.toString(x.getValue("content".getBytes(), HBaseCommentDetailKey.sku.getBytes))
      val userProvince = Bytes.toString(x.getValue("content".getBytes(), HBaseCommentDetailKey.userProvince.getBytes))
      val creationTime = Bytes.toString(x.getValue("content".getBytes(), HBaseCommentDetailKey.creationTime.getBytes))
      /* case class HBaseJDCommentModel(
                                     val commentID:String, val productID:String,
                                     val userProvince: String, val creationTime: String
                                   )*/
      JDCommentModel(rowKey, commentID, productID, userProvince, creationTime)
  }



  def param_11 =(c:(String,String)) =>{(Map(c._1->1),c._2)}
  def param_12 = (c:(Map[String,Int],String),newC:(String,String)) => {
    var _1:Map[String,Int] =null
    var _2:String = null
    //计算区域
    if(c._1.keySet.contains(newC._1))
    { val intValue:Int=c._1(newC._1)+1
      _1 =c._1.+(newC._1->intValue)
    }
    else {_1 =c._1+(newC._1->1)}
    //计算上架时间
    if(c._2.compareTo(newC._2) > 0) _2 =newC._2 else _2 =c._2
    (_1,_2)
  }
  def param_13 = (c:(Map[String,Int],String),newC:(Map[String,Int],String)) => {
    var _1:Map[String,Int]= null
    var _2:String=null
    _1 = c._1.++(newC._1)
    if(c._2.compareTo(newC._2) > 0) _2 =newC._2
    else _2 =c._2

    (_1,_2)
  }
  def ReduceProvinceAndTime(productArray:Array[JDProductModel]) = {

    val hBaseRDD=HBaseDao.scanTableBySparkAPI(ConstantUtils.hBaseJDCommentTable)
    val commentRDD = hBaseRDD.map(MapFromResultToJDCommentModel(_))
    val rdd=commentRDD.filter { r =>
      var flag =false
      for (product <- productArray) {
        if(r.productID.equals(product.productID)){
          flag =true
        }
      }
      flag
    }
    //计算区域人数+上架时间
    val resultArray=rdd.map(r=>(r.productID,(r.userProvince,r.creationTime))).combineByKey(param_11,param_12,param_13).collect()

    resultArray.foreach(r=>println(r))

    val newResultArray=resultArray.sortBy(r=>r._1)
    var newProductArray =productArray.sortBy(r=>r.productID)

    if(newResultArray.length != newProductArray.length) {
      println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx错误:")
    }
    val i =0
    while(i<newProductArray.length) {
      if(newProductArray.apply(i).productID.equals( newResultArray.apply(i)._1)){
        newProductArray.apply(i).productSalesTime = newResultArray.apply(i)._2._2
        newProductArray.apply(i).userProvince = newResultArray.apply(i)._2._1.mkString(" ")
      }
    }
    //rdd.unpersist()
    spark.sparkContext.parallelize(newProductArray).toDS().write.mode("append").jdbc("jdbc:mysql://192.168.10.86:3306/test", "product", MysqlUtils.getProperties())
  }
  def ReduceHotsell(productModelArray:Array[JDProductModel])= {
    val hotsellRDD = MapService.getJDHotSellRDD(HBaseDao.scanTableBySparkAPI(ConstantUtils.hBaseJDHotsellTable))
    val rdd =hotsellRDD.filter{r=>
      var flag =false
      for(product<-productModelArray){
        //爆品表hotID ==产品表hotsellID
        if(r.productHotID.equals(product.productHotSellID)){
          flag = true
        }
      }
      flag
    }
    rdd.toDF().write.mode("append").jdbc("jdbc:mysql://192.168.10.86:3306/test", "hotsell", MysqlUtils.getProperties())
  }

  def main(args: Array[String]): Unit = {
    ReduceProvinceAndTime(productArray)
    ReduceHotsell(productArray)
  }






}
