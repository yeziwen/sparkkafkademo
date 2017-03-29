import java.text.SimpleDateFormat
import java.util.Calendar

import org.apache.spark.sql.SparkSession

/**
  * Created by ye on 2017/3/20.
  */
object SparkUtils {

  def getSpark():SparkSession = {
    val spark:SparkSession = SparkSession.builder().appName("app").master("local[2]").getOrCreate()
    return  spark
  }


}
