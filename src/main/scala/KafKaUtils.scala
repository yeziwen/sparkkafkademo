/**
  * Created by daiquanyi on 2017/2/9.
  */
object KafKaUtils {

  object Kafka2Spark {

    /**
      *
      */
    val ZOOKEEPER_CONNECT = ("zookeeper.connect",
      "192.168.10.91:2181," +
      "192.168.10.92:2181," +
      "192.168.10.93:2181," +
      "192.168.10.94:2181," +
      "192.168.10.95:2181")

    /**
      *可不用
      */
    val METADATA_BROKER_LIST = ("metadata.broker.list",
      "192.168.10.91:9092," +
        "192.168.10.92:9092," +
        "192.168.10.93:9092," +
        "192.168.10.94:9092," +
        "192.168.10.95:9092")

    val TOPICS = "10jqka-com-cn," +
      "baidu-com," +
      "ele-me," +
      "jd-com," +
      "jin10-com," +
      "lagou-com," +
      "meituan-com," +
      "tianyancha-com," +
      "tmall-com," +
      "tootoo-cn," +
      "xueqiu-com," +
      "yhd-com,"+
      "lagou-com," +
      "kanzhun-com"

    val GROUP_ID = ("group.id", "yeziwen")

    val AUTO_OFFSET_RESET = ("auto.offset.reset", "smallest")

    val SERIALIZER_CLASS = ("serializer.class", "kafka.serializer.StringEncoder")

    val AUTO_COMMIT_INTERVALMS = ("auto.commit.interval.ms", "1000")

    val PARTITION_ASSIGNMENT_STRATEGY = ("partition.assignment.strategy", "roundrobin")

  }

}
