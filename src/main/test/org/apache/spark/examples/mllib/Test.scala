package org.apache.spark.examples.mllib

import org.apache.spark.mllib.feature.HashingTF

/**
  * Created by ye on 2017/3/27.
  */
object Test {

  def main(args: Array[String]): Unit = {

    val spark = SparkUtils.getSpark()

    val spam = spark.sparkContext.textFile("spam.txt")

    val normal = spark.sparkContext.textFile("normal.txt")

    val tf = new HashingTF(numFeatures = 100)

    val spamFeatures = spam.map(email => tf.transform(email.split(" ")))

    val normalFeatures = normal.map(email => tf.transform(email.split(" ")))


  }

}
