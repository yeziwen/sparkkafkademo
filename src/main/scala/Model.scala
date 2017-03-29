

//hbase爆品主键=商品ID+版本字段+爆品ID
//mysql爆品的主键=商品ID+爆品ID
//当前主键=商品ID+爆品ID
case class JDHotSellModel(
                          val platformID:Int, val sparkID:String, val productHotID: String, val productID: String,
                          val productName: String, val productSpec: String, val productPrice: String,
                          val productURL: String, val downloadTime: String, val productImage: String
                        )


case class  JDCommentModel(val rowKey:String,
                                val commentID:String, val productID:String,
                                val userProvince: String, val creationTime: String
                              )


case class  JDProductModel(val index:Int, val productHotSellID:String,var userProvince:String,val commentCount:Int, val sparkID:String, val platformID:Int, val productID:String,
                           val productName:String, val productBrand:String, val productSpec:String,
                           val productPrice:String, var productArea:String, var productSalesTime:String,
                           val standardCategory:String, val category:String, val salesEvents:String,
                           val productURL:String,
                           val hasFittingsRecommend:String, val downloadTime:String
                          )



