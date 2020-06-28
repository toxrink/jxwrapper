import x.remote.SftpWrapper
import x.remote.SftpInfo
object SftpTest {
  def main(args: Array[String]): Unit = {
    val sftpInfo = new SftpInfo()
    sftpInfo.setLocal("D:\\var\\log")
    // sftpInfo.setLocal("D:\\var\\log\\goflume")
    sftpInfo.setRemote("/data/test")
    sftpInfo.setHost("192.168.119.206")
    sftpInfo.setUsername("root")
    sftpInfo.setPassword("root")
   
    // SftpWrapper.scp(sftpInfo)
    SftpWrapper.get(sftpInfo)
  }
}
