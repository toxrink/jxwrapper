import java.io.File

import org.apache.commons.io.IOUtils
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

//    val remoteOne = RemoteWrapper.wrap("haha", null, 17516, new Rmi() {
//    })
//    remoteOne.getServer.start()
    val run = "D:\\flume_ui\\flume\\tools\\vap-flume-tools.bat"
    val p = "-pC:\\Users\\admin\\Desktop\\test23.js"
    val d =
      "-dWinLog=应用程序 Type=信息 Category=2 DateTime=2017-03-01 09:27:37 Source=UPLive EventID=8 Computer=王竹 User=wangzhu Desc=更改卷影复制服务的 SCM 状态时遇到异常错误: [下载升级信息文件成功,]。";
    val h = "-h{'_TYPE':'WinLog'}"
//    val process = CmdWrapper.run("ls /")
    val process = CmdWrapper.runWithEnv(Array(run, "js", p, d, h))
    println("1\t" + process.toStringFromInput("GBK"))
    println("2\t" + process.toStringFromError("GBK"))

    val in = IOUtils.toString(process.getProcess.getInputStream, "GBK")
//    val err = IOUtils.toString(process.getProcess.getErrorStream, "GBK")
    System.out.println(in)
//    System.out.println(err)
//    process.destroy
  }
}
