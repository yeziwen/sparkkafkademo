package test

import java.io.{BufferedReader, File, FileReader, StringReader}

import org.apache.commons.io.FileUtils

import scala.collection.JavaConverters._

/**
  * Created by ye on 2017/3/16.
  */


object ImplicitUitls {

  implicit class Files(file: File) {

    def lines(): List[String] = {

      FileUtils.readLines(file).asScala.toList

    }


    def mapxx(fn: String => String): Unit = {
      val source = this.lines()

      source.map {
        r => fn(r)
      }
    }


    def test(fn: String => String): String = {
      "ddd"
    }

  }





  def funcParam():String = {
    "dd"
  }

  def main(args: Array[String]): Unit = {



    val filepath = "C:\\Users\\ye\\Desktop\\brand.txt"
    val file = new File(filepath)

    val d = Array("", "")


    file.mapxx{x=>
      println(x)
      x
    }

  }


}
