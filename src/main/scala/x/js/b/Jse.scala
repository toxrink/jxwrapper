package x.js.b

import javax.script.{Invocable, ScriptEngine, ScriptEngineManager, ScriptException}

import scala.io.Source

/**
  * Created by xw on 2019/8/1.
  */
class Jse[T](jsPath: String) {
  def setFunction(method: String): x.js.i.Function[T] = {
    FunctionT(jsPath, method)
  }

  case class FunctionT(jsPath: String, method: String) extends x.js.i.Function[T] {
    var manager: ScriptEngineManager = new ScriptEngineManager()
    var engine: ScriptEngine         = manager.getEngineByName("nashorn")

    val reader = Source.fromFile(jsPath, "UTF-8").bufferedReader()
    engine.eval(reader)
    reader.close()

    @throws[NoSuchMethodException]
    @throws[ScriptException]
    override def call(param: T): T = {
      val run   = engine.asInstanceOf[Invocable]
      val value = run.invokeFunction(method, param.asInstanceOf[Object])
      value.asInstanceOf[T]
    }
  }

}
