package test

import org.apache.http.client.methods
import org.apache.http.client.methods.HttpPost

import scala.collection.mutable
import scala.io.Source

/**
  * Created by ye on 2017/3/16.
  */
object Func {



 /* def  login(callBack:()  =>  String)  =  {
    //耗时操作
    HttpPost.start{
          //自定义响应逻辑callBack
    }
    println(op1())

  }//无参数*/

  def  func1(op:(String)  =>  String)  = {

    println(op("test")) //1个参数

  }


  def  func2(op:(String,String)  =>  String)  = {
    val a = ""

    //发送请求
    //相应处理
    op(a,a)
  }

  val regex = """<title>(.*?)</title>""".r
  def scalaSource() ={
    val queue = new mutable.Queue[String]()
    val depth = 0
    val url = "http://www.baidu.com"
    queue.enqueue(url)
    while(queue.size>0) {
      //把队列中首个元素弹出
      val urlStr = queue.dequeue()
      val result = Source.fromURL(urlStr)
     // println()
     val finalArray = regex.findAllIn(result.getLines().mkString)
     for(ele <- finalArray)
       println(ele)
    }
  }

  def main(args: Array[String]): Unit = {
    scalaSource()
   /* println("xx")
    scala.io.Source.fromURL("http:/")*/



  }



}
