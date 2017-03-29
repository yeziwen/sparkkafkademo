import java.text.SimpleDateFormat
import java.util.Calendar

/**
  * Created by ye on 2017/3/6.
  */
object ConstantUtils {

  var category1 = "洗发"
  var category2 = "护发"
  var category3 = "沐浴露"
  var category4 = "牙膏"
  var category5 = "护肤品"          //以上按照默認索引排名
  var category6 = "日本进口零食"   //按照評論數排名
  var categoryArrays=Array(category1,category2,category3,category4,category5,category6)

  val mysqlHotsellTable="hotsell"
  val mysqlProductTable="product"
  val mysqlCommentTable="comment"
  val hBaseJDHotsellTable="JDHotShell"
  val hBaseJDProductTable="JDProductDetail"
  val hBaseJDCommentTable="JDCommentDetail"


  val spd :SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  def get3DayTimeString():String = {
    val cn: Calendar = Calendar.getInstance()
    cn.add(Calendar.DAY_OF_MONTH, -3)
    spd.format(cn.getTime)
  }
}

