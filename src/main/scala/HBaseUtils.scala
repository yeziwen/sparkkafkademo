import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.{CompareFilter, RegexStringComparator, RowFilter}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Base64

/**
  * Created by ye on 2017/3/4.
  */
object HBaseUtils {

  val conf :Configuration= HBaseConfiguration.create()
  conf.set("hbase.zookeeper.quorum",
    "192.168.10.81,192.168.10.82,192.168.10.83,192.168.10.84,192.168.10.85,192.168.10.86,192.168.10.87")
  conf.set("hbase.zookeeper.property.clientPort", "2181")

  def getConf():Configuration ={
    return  conf
  }




  def getConf(tableName:String):Configuration ={
    conf.set(TableInputFormat.INPUT_TABLE, tableName)
//行过滤
    conf.set(TableInputFormat.SCAN,Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))
    return  conf
  }


  def getConf(tableName:String,regexString:String):Configuration ={
    val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(regexString)) // OK 筛
    scan.setFilter(filter)
    conf.set(TableInputFormat.INPUT_TABLE, tableName)
    conf.set(TableInputFormat.SCAN,Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))
    return  conf
  }
  val scan  = new Scan()









}
