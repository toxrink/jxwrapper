package x.js

import x.js.b.Jse
import x.js.i.Function

/**
 * Created by xw on 2019/8/1.
 */
object JsWrapper {


  /**
   * 加载js脚本
   *
   * @param jsPath javascript 文件路径
   * @param method 入口方法名称
   * @return
   */
  @throws[Exception]
  def loadJs[T](jsPath: String, method: String): Function[T] = new Jse[T](jsPath).setFunction(method)
}
