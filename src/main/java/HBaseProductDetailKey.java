/**
 * Created by ye on 2017/2/27.
 */
public   class HBaseProductDetailKey {
    
    public static 	String	platformID	=	"platformID"	; //	平台ID
    public static 	String	shopID	=	"shopID"	; //	店铺ID
    public static 	String	sku	=	"productID"	; //	商品ID
    public static 	String	productName	=	"productName"	; //	商品名称
    public static 	String	productCode	=	"productCode"	; //	商品条码
    public static 	String	productBrand	=	"productBrand"	; //	商品品牌
    public static 	String	productSpec	=	"productSpec"	; //	商品规格
    public static 	String	productPrice	=	"productPrice"	; //	商品价格
    public static 	String	commentCount	=	"commentCount"	; //	商品评论数
    public static 	String	enshrineCount	=	"enshrineCount"	; //	商品收藏数
    public static 	String	monthlySales	=	"monthlySales"	; //	商品月销量
    public static 	String	salesEvents	=	"salesEvents"	; //	促销活动
    public static 	String	productArea	=	"productArea"	; //	商品产地
    public static 	String	productSalesTime	=	"productSalesTime"	; //	商品上架时间
    public static 	String	fitPeople	=	"fitPeople"	; //	适合人群
    public static 	String	regionalism	=	"regionalism"	; //	区域特点
    public static 	String	isSelfBusiness	=	"isSelfBusiness"	; //	是否自营
    public static 	String	category	=	"category"	; //	商品类别
    public static 	String	productHotSellID	=	"productHotSellID"	; //	爆品的关联ID
    public static 	String	productURL	=	"productURL"	; //	链接
    public static 	String	imageURL	=	"imageURL"	; //	商品的图片的链接

    public static 	String	commentGoodRate	=	"commentGoodRate"	; //	好评率
    public static 	String	commentGeneralRate	=	"commentGeneralRate"	; //	中评率
    public static 	String	commentPoorRate	=	"commentPoorRate"	; //	差评率
    public static 	String	commentGoodCount	=	"commentGoodCount"	; //	好评数
    public static 	String	commentGeneralCount	=	"commentGeneralCount"	; //	中评数
    public static 	String	commentPoorCount	=	"commentPoorCount"	; //	差评数
    public static 	String	averageScore	=	"averageScore"	; //	平均得分
    public static 	String	imageListCount	=	"imageListCount"	; //	晒图数
    public static 	String	hotCommentTagStatistics	=	"hotCommentTagStatistics"	; //	热门关键字标签（字符串数组）


    //新增字段
    public static String  downloadTime ="downloadTime";
    public static String  index = "index";
    public static String  standardCategory="standardCategory"; //类别
    public static String  pageModelKey="pageModelKey"; //接口Key
    public static String  isWorldWide="isWorldWide"; //是否全球购
    public static String  venderid="venderid"; //供应商ID
    public static String  shopName="shopName"; //
    public static  String productStockCount="productStockCount";//商品库存数量

    public static String isSoldout = "isSoldout";
    public static String basicPrice = "basicPrice";

    public static String hasFittingsRecommend = "hasFittingsRecommend";

    public static String jdProductDetailDownloadTime = "jdProductDetailDownloadTime";
    public static String jdProductListDownloadTime = "jdProductListDownloadTime";
    public static String jdCommentDownloadTime = "jdCommentDownloadTime";
    public static String jdHotSellDownloadTime = "jdHotSellDownloadTime";
    public static String jdSearchDownloadTime = "jdSearchDownloadTime";
    public static String jdHomeDownloadTime = "jdHomeDownloadTime";

  //  public static String  title="title"; //JD-商品详情的title有时候比name丰富。name精准
}
