package x.js

import javax.script.{Invocable, ScriptEngine, ScriptEngineManager, ScriptException}
import x.js.i.Function

import scala.io.Source

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
  def loadJs[T](jsPath: String, method: String): Function[T] = DefaultFunction(jsPath, method)

  case class DefaultFunction[T](jsPath: String, method: String) extends x.js.i.Function[T] {
    var manager: ScriptEngineManager = new ScriptEngineManager()
    var engine: ScriptEngine = manager.getEngineByName("nashorn")

    private val reader = Source.fromFile(jsPath, "UTF-8").bufferedReader()
    engine.eval(reader)
    reader.close()

    @throws[NoSuchMethodException]
    @throws[ScriptException]
    override def call(param: T): T = {
      val run = engine.asInstanceOf[Invocable]
      val value = run.invokeFunction(method, param.asInstanceOf[Object])
      value.asInstanceOf[T]
    }
  }
}
