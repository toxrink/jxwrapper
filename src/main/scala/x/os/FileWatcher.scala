package x.os

/**
  * Created by xw on 2019/8/1.
  */
trait FileWatcher {
  /**
    * 数据处理方法
    *
    * @param msg
    */
  def push(msg: String): Unit

  /**
    * 是否停止监听
    *
    * @return
    */
  def isStop: Boolean
}
