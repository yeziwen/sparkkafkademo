package test

import java.io._
import java.net.{InetAddress, InetSocketAddress, ServerSocket, Socket}
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.util.concurrent.Executors

/**
  * Created by ye on 2017/3/16.
  */
class TimeServerHandler(var socket:Socket) extends Runnable {
  override def run(): Unit = {
      try {
        val in = new BufferedReader(new InputStreamReader(socket.getInputStream))
        val out2 = new PrintWriter(socket.getOutputStream, true)
        while (true) {
          val s = in.readLine()
          println(s)
          out2.println(Thread.currentThread().getName
            +"response.....")
        }
        //val out =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream))
      } catch {
        case e =>
      }
  }
}

object TimeServer {


  def vesion01() ={
    var port = 8080
    var server: ServerSocket = null
    server = new ServerSocket(port)
    val excutor =Executors.newFixedThreadPool(3)
    while(true) {
      var socket: Socket = null
      socket = server.accept()
      val timeServerHandler =new TimeServerHandler(socket)
      excutor.submit(timeServerHandler)
      //  new Thread().start()
    }
  }

  def version02() = {


  }

  def main(args: Array[String]): Unit = {



    //
  }

}
