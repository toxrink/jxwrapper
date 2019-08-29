package x.rmi

import java.rmi.registry.LocateRegistry

import x.utils.JxUtils

/**
  * Created by xw on 2019/8/2.
  */
object RemoteWrapper {

  /**
    * 创建服务
    *
    * @param name 绑定服务名称
    * @param host 绑定的ip
    * @param port 绑定的端口
    * @param rmi  绑定的服务
    * @return
    */
  def wrap(name: String, host: String, port: Int, rmi: Rmi): RemoteOne = {
    val ro = RemoteOne()
    ro.remoteServer = RemoteServer(port, name, new RemoteService[Rmi] {
      override def apply: Rmi = rmi
    })
    ro.remoteClient = RemoteClient(name, host, port)
    ro
  }

  trait RemoteService[T <: Rmi] {
    def apply: T
  }

  case class RemoteServer(port: Int, name: String, server: RemoteService[Rmi]) {
    private val log = JxUtils.getLogger(this.getClass)

    def start(): Unit = {
      try {
        LocateRegistry.getRegistry(port).unbind(name)
      } catch {
        case _: Throwable =>
      }
      log.info(s"bind rmi service<$name> at $port")
      val reg = LocateRegistry.createRegistry(port)
      val service = server.apply
      reg.bind(name, service)
    }
  }

  case class RemoteClient(name: String, host: String = "localhost", port: Int) {
    private val log = JxUtils.getLogger(this.getClass)

    def connect[T <: Rmi](): T = {
      log.info(s"lookup rmi service<$name> at $host:$port")
      val registry = LocateRegistry.getRegistry(host, port)
      registry.lookup(name).asInstanceOf[T]
    }
  }

  case class RemoteOne() {
    var remoteServer: RemoteServer = _
    var remoteClient: RemoteClient = _

    def getServer: RemoteServer = {
      remoteServer
    }

    def getClient: RemoteClient = {
      remoteClient
    }
  }

}
