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
      rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category2)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))
      rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category3)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))
      rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category4)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))
      rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category5)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))
      rdd1.filter(r=>r.standardCategory.equals(ConstantUtils.category6)).top(100)(ReduceService.ordering.reverse).foreach(r=>println(r))*/

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

  def ReduceProvince(productArray:Array[JDProductModel]) = {

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

    rdd.persist()

    /*//计算上架时间
    val param = (c:String,newC:String) => {
      if(c.compareTo(newC) > 0) newC else c
    }
    rdd.map(r=>(r.productID,r.creationTime)).combineByKey(c=>c,param,param).collect()

    //计算区域人数

    val param_p1 = (c:Map[String,Int],newC:String) => {
      if(c.keySet.contains(newC))
      {
        val intValue:Int=c(newC)+1
        c.+(newC->intValue)
      }
      else c.+(newC->1)
    }

    val param_p2 = (c:Map[String,Int],newC:Map[String,Int]) => {
      c.++(newC)

    }*/

  //  rdd.map(r=>(r.productID,r.userProvince)).combineByKey(c=>Map(c,1),param_p1,param_p2)





    rdd.unpersist()
  }


  def printHotsell(productModelArray:Array[JDProductModel])= {

    val hotsellRDD = MapService.getJDHotSellRDD(HBaseDao.scanTableBySparkAPI(ConstantUtils.hBaseJDHotsellTable))
    //过滤
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








}
