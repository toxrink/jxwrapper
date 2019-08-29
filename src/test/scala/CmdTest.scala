import java.io.File

import x.os.{CmdWrapper, FileWatcher}
import x.rmi.{RemoteWrapper, Rmi}

/**
  * Created by xw on 2019/8/1.
  */
object CmdTest {
  def main(args: Array[String]): Unit = {
    //    CmdWrapper.sleep(1000)
    //    CmdWrapper.mkdirs("f:/test/haha")
    //    CmdWrapper.tailf(new FileWatcher {
    //      /**
    //        * 数据处理方法
    //        *
    //        * @param msg
    //        */
    //      override def push(msg: String): Unit = println(msg)
    //
    //      /**
    //        * 是否停止监听
    //        *
    //        * @return
    //        */
    //      override def isStop: Boolean = false
    //    }, new File("C:\\Users\\admin\\Desktop\\a.js"), 0, 0)

    val remoteOne = RemoteWrapper.wrap("haha", null, 17516, new Rmi() {
    })
    remoteOne.getServer.start()
  }
}
