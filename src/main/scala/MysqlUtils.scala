import java.util.Properties

/**
  * Created by ye on 2017/3/20.
  */
object MysqlUtils {
  val prop = new Properties()
  prop.put("user", "root")
  prop.put("password", "123456")
  prop.put("driver", "com.mysql.jdbc.Driver")
  def getProperties(): Properties = {
    prop
  }
}
