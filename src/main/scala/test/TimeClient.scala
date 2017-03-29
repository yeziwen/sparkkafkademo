package test

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

/**
  * Created by ye on 2017/3/16.
  */
class TimeClientTask extends Runnable {
  override def run(): Unit = {


    val port = 8080
    val socket = new Socket("127.0.01", port)
    val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val out = new PrintWriter(socket.getOutputStream, true)

    while(true) {

      Thread.sleep(2000)
      out.println("客戶端發送的信息："+Thread.currentThread().getName+"client request...")
      Thread.sleep(2000)
      val str = in.readLine()//IO阻塞
      println(str)

    }

  }
}
object TimeClient {


  def main(args: Array[String]): Unit = {


     new Thread(new TimeClientTask()).start()
     new Thread(new TimeClientTask()).start()
     new Thread(new TimeClientTask()).start()
     new Thread(new TimeClientTask()).start()

  }

}
