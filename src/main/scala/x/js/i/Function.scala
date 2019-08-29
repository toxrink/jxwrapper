package x.js.i

import javax.script.ScriptException

/**
  * Created by xw on 2019/8/1.
  */
@FunctionalInterface
trait Function[T] {
  @throws[NoSuchMethodException]
  @throws[ScriptException]
  def call(param: T): T
}
