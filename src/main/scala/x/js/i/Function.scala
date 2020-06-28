package x.js.i

import javax.script.ScriptException

/**
  * Created by xw on 2019/8/1.
  */
@FunctionalInterface
trait Function[T] {

  /**
    * 调用js
    *
    * @param param js入库方法参数
    * @return
    */
  @throws[NoSuchMethodException]
  @throws[ScriptException]
  def call(param: T): T
}
