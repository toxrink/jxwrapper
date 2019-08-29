package x.js

/**
  * Created by xw on 2019/8/1.
  */
object JsWrapper {

  import x.js.b.Jse

  /**
    *
    * @param jsPath javascript 文件路径
    * @param method 入口方法名称
    * @return
    * @throws Exception
    */
  @throws[Exception]
  def loadJs[T](jsPath: String, method: String): x.js.i.Function[T] = new Jse[T](jsPath).setFunction(method)
}
