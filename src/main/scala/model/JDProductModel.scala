package model

/**
  * Created by ye on 2017/3/6.
  */

case class  JDProductModel(val index:Int, val productHotSellID:String,var userProvince:String,val commentCount:Int, val sparkID:String, val platformID:Int, val productID:String,
                           val productName:String, val productBrand:String, val productSpec:String,
                           val productPrice:String, var productArea:String, var productSalesTime:String,
                           val standardCategory:String, val category:String, val salesEvents:String,
                           val productURL:String,
                           val hasFittingsRecommend:String, val downloadTime:String
)
