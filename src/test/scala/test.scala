/**
  * Created by xw on 2019/11/14.
  */
object test {
  def main(args: Array[String]): Unit = {
    import org.apache.commons.io.IOUtils
    import java.io.IOException
    val run = "D:\\flume_ui\\flume\\tools\\vap-flume-tools.bat"
    val p = "-pC:\\Users\\admin\\Desktop\\test23.js"
    val d =
      "-dWinLog=应用程序 Type=信息 Category=2 DateTime=2017-03-01 09:27:37 Source=UPLive EventID=8 Computer=王竹 User=wangzhu Desc=更改卷影复制服务的 SCM 状态时遇到异常错误: [下载升级信息文件成功,]。"
    val h = "-h{'_TYPE':'WinLog'}"
    try {
      val processBuilder = new ProcessBuilder(run)
//      System.out.println(processBuilder.environment)
      val process = processBuilder.start
      val in = IOUtils.toString(process.getInputStream)
      val err = IOUtils.toString(process.getErrorStream)
      System.out.println(in)
      System.out.println(err)
      process.destroy()
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}
