package test.hbase

import java.io.IOException
import java.util

import com.google.gson.reflect.TypeToken
import com.google.gson.{Gson, JsonObject, JsonParser}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ye on 2017/2/14.
  */

object SparkReadHbase {
  def pi(spark: SparkSession): Unit = {
    val slices = 2
    val n = math.min(100000L * slices, Int.MaxValue).toInt
    // avoid overflow
    val random = Math.random()
    val count = spark.sparkContext.parallelize(1 until n, slices).map { i =>
      val x = random * 2 - 1
      val y = random * 2 - 1
      if (x * x + y * y < 1) 1 else 0
    }.reduce(_ + _)

    spark.stop()
  }

  def main(args: Array[String]): Unit = {
  //  var data: MyData = new Gson().fromJson("""{"年龄":20}""", new TypeToken[MyData]() {}.getType())
   // println(data.年龄)




    // 本地模式运行,便于测试
    val spark = SparkSession.builder().master("local").appName("HBaseTest").getOrCreate()
    import spark.implicits._

    val rdd = spark.sparkContext.parallelize(Array("cc", "xx"), 2)


    // 创建hbase configuration
    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set(TableInputFormat.INPUT_TABLE, "jd-com")
    // 创建 spark context
    // 从数据源获取数据
    /** 1:spark利用newAPIHadoopRDD读取hbase数据不完整的问题
      * 2:mapreduce.ableInputFormat:这是专门处理基于HBase的MapReduce的输入数据的格式类
      */
    // 将数据映射为表  也就是将 RDD转化为 dataframe schema
    //元祖RDD--》元组DataSet
    /**
      * DataFrame引入了schema和off-heap
      *
      * schema : RDD每一行的数据, 结构都是一样的. 这个结构就存储在schema中.
      * Spark通过schame就能够读懂数据, 因此在通信和IO时就只需要序列化和反序列化数据, 而结构的部分就可以省略了
      * off-heap : 意味着JVM堆以外的内存, 这些内存直接受操作系统管理（而不是JVM）。Spark能够以二进制的形式序列化数据(不包括结构)到off-heap中,
      * 当要操作数据时, 就直接操作off-heap内存. 由于Spark理解schema, 所以知道该如何操作
      * off-heap就像地盘, schema就像地图, Spark有地图又有自己地盘了, 就可以自己说了算了, 不再受JVM的限制, 也就不再收GC的困扰了.
      *
      * RDD缺点：只有数据,没有结构信息
      * RDD优点：
      * 通过schema和off-heap, DataFrame解决了RDD的缺点, 但是却丢了RDD的优点
      *
      * DataFrame不是类型安全的,
      * API也不是面向对象风格的.
      */
    /*    val shopRDD = hbaseRDD.map {r=>(
          /**
            * info ==列族
            * customer_id =》列名
            */
          Bytes.toString(r._2.getValue(Bytes.toBytes("content"),Bytes.toBytes("DownloadTime"))),
          Bytes.toString(r._2.getValue(Bytes.toBytes("content"),Bytes.toBytes("PageModelName"))),
          Bytes.toString(r._2.getValue(Bytes.toBytes("content"),Bytes.toBytes("PageKey"))),
          Bytes.toString(r._2.getValue(Bytes.toBytes("content"),Bytes.toBytes("MyData")))
        )}.toDF("DownloadTime","PageModelName","PageKey","MyData")
         shopDF.printSchema()
        shopDF.show()
        */
    val primaryRDD=spark.sparkContext.newAPIHadoopRDD(hBaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
      .map { r =>
        Bytes.toString(r._2.getValue(Bytes.toBytes("content"), Bytes.toBytes("MyData")))
      }

   val secondRDD= primaryRDD.map { r =>
        //使用外部JSON API来解析rdd每行（json）解析一层

        //jsonString="""{"name":"yzw","age":1}"""
        var map: java.util.HashMap[String, String] = new Gson().fromJson(r, new TypeToken[java.util.HashMap[String, Object]]() {}.getType())
        var iterable: util.Iterator[java.util.Map.Entry[String, String]] = map.entrySet().iterator()
        var sb: StringBuilder = new StringBuilder()
        while (iterable.hasNext) {
          var entity: java.util.Map.Entry[String, String] = iterable.next()
          var key = entity.getKey
          var value = entity.getValue
          sb.append(key + ":" + value + "*******")
        }
        sb.toString()
      }

    primaryRDD.take(10).foreach(x =>
      println(x))


    //  val shopDF=valueRDD
    //
    //调用外部api来解析json得到数据和信息
    // 测试
    /* shopDF.createTempView("shop")
     val df2 = spark.sqlContext.sql("SELECT DownloadTime,PageModelName FROM shop").foreach(x=>println(x))*/
    //r._2的类型是Result
  }

  /*  val sparkConf = new SparkConf().setAppName("HBaseTest").setMaster("local")
    val sc = new SparkContext(sparkConf)

    val tablename = "tianhong_demo"
    val conf = HBaseConfiguration.create()
    //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
    conf.set("hbase.zookeeper.quorum", "192.168.10.91,192.168.10.92,192.168.10.93,192.168.10.94,192.168.10.95")
    //设置zookeeper连接端口，默认2181
    conf.set("hbase.zookeeper.property.clientPort", "2181")

    /*  //设置输出格式和表名
      val jobConf = new JobConf(hbaseConf, this.getClass)
      //jobConf.setOutputFormat(classOf[TableOutputFormat])
      jobConf.setOutputFormat(classOf[TableOutputFormat[]])
      jobConf.set(TableOutputFormat.OUTPUT_TABLE, "iteblog")*/
    // 如果表不存在则创建表
    val admin = new HBaseAdmin(conf)
    if (!admin.isTableAvailable(tablename)) {
      val tableDesc = new HTableDescriptor(TableName.valueOf(tablename))
      admin.createTable(tableDesc)
    }
    //写入数据

    val hBaseRDD_ = sc.newAPIHadoopRDD(
      conf,
      classOf[TableOutputFormat[ImmutableBytesWritable]],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])


    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(
      conf,
      classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])

    val count = hBaseRDD.count()
    println("xxxxxxxxxx" + count)
    val indataRDD = sc.parallelize(Array("1,jack,15","2,Lily,16","3,mike,16"))
    hBaseRDD.foreach { case (_, result) => {
      //获取行键
      val key = Bytes.toString(result.getRow)
      //通过列族和列名获取列
      val name = Bytes.toString(result.getValue("cf".getBytes, "name".getBytes))
      val age = Bytes.toInt(result.getValue("cf".getBytes, "age".getBytes))
      println("Row key:" + key + " Name:" + name + " Age:" + age)
    }
    }

    sc.stop()

    admin.close()
  }
}

*/
}
