package test.version01

import java.net.InetSocketAddress
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.util.concurrent.Executors

import test.TimeServerHandler

import scala.collection.JavaConverters._

/**
  * Created by ye on 2017/3/17.
  */
class MultiplexerTimeServer(val port:Int) extends Runnable {

  var selctor: Selector = null
  selctor = Selector.open()
  new Thread().start()


  var serverSocketChannel: ServerSocketChannel = null
  serverSocketChannel ==ServerSocketChannel.open()
  serverSocketChannel.configureBlocking(false)
  serverSocketChannel.socket().bind(new InetSocketAddress(port),1024)

  var key:SelectionKey = serverSocketChannel.register(selctor,SelectionKey.OP_ACCEPT)

  val excutor =Executors.newFixedThreadPool(3)

  override def run(): Unit = {

    while(true) {
        selctor.select(1000)//选择出准备就绪的Channel

        var selectedKeys =selctor.selectedKeys

      var socketChannel: SocketChannel = null
      //socketChannel =
     // val timeServerHandler =new TimeServerHandler(socketChannel)
     // excutor.submit(timeServerHandler)
      //  new Thread().start()
    }
  }
}
object MultiplexerTimeServer {

}
