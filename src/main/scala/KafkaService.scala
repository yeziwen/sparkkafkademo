import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.ReceiverInputDStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaService {



  def consumer() {
    val conf = new SparkConf()
    conf.setMaster("local[2]")
    conf.setAppName("testApp")
    val ssc = new StreamingContext(conf, Seconds(1))
    ssc.checkpoint("checkpoint")
    //?
    val topicMap = KafKaUtils.Kafka2Spark.TOPICS.split(",").map((_, 1)).toMap
    val kafkaParams = Map[String, String](
      KafKaUtils.Kafka2Spark.GROUP_ID._1 -> KafKaUtils.Kafka2Spark.GROUP_ID._2,
      KafKaUtils.Kafka2Spark.ZOOKEEPER_CONNECT._1 -> KafKaUtils.Kafka2Spark.ZOOKEEPER_CONNECT._2,
      KafKaUtils.Kafka2Spark.AUTO_OFFSET_RESET._1 -> KafKaUtils.Kafka2Spark.AUTO_OFFSET_RESET._2,
      KafKaUtils.Kafka2Spark.SERIALIZER_CLASS._1 -> KafKaUtils.Kafka2Spark.SERIALIZER_CLASS._2,
      KafKaUtils.Kafka2Spark.AUTO_COMMIT_INTERVALMS._1 -> KafKaUtils.Kafka2Spark.AUTO_COMMIT_INTERVALMS._2,
      KafKaUtils.Kafka2Spark.PARTITION_ASSIGNMENT_STRATEGY._1 -> KafKaUtils.Kafka2Spark.PARTITION_ASSIGNMENT_STRATEGY._2)
    val dStream = KafkaUtils.createStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topicMap, StorageLevel.MEMORY_AND_DISK)

    print(dStream)

    ssc.start()
    ssc.awaitTermination()
  }


  def print(ds: ReceiverInputDStream[(String, String)]): Any = {
    ds.foreachRDD {
      rdd => {
        rdd.foreachPartition {
          partitionOfRecords =>
            partitionOfRecords.foreach { r =>
              println(r)

            }

        }
      }
    }
  }
}