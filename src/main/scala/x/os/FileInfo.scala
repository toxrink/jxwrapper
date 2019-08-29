package x.os

import java.io.File

import scala.beans.BeanProperty

/**
  * Created by xw on 2019/8/1.
  */
case class FileInfo(file: File) {
  @BeanProperty
  var path: String = if (null == file) "" else file.getAbsolutePath
  @BeanProperty
  var name: String = if (null == file) "" else file.getName

  def this() = {
    this(null)
  }
}
