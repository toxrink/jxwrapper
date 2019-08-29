package x.utils

import java.lang.reflect.Field
import java.util
import java.util.function.Predicate
import java.util.stream.Collectors

import org.apache.commons.lang.StringUtils
import x.common.annotation.ConfigValue

/**
 * Created by xw on 2019/8/28.
 */
object ReflectUtils {
  private val LOG = JxUtils.getLogger(ReflectUtils.getClass)

  def invokeValue[T](obj: Object, method: String): T = {
    obj.getClass.getMethod(method).invoke(obj).asInstanceOf[T]
  }

  def loadNewInstance[T](clazz: String): T = {
    newInstance(ReflectUtils.getClass.getClassLoader.loadClass(clazz))
  }

  def newInstance[T](clazz: String): T = newInstance(Class.forName(clazz))

  def newInstance[T](clazz: Class[_]): T = clazz.newInstance().asInstanceOf[T]

  def newInstance[T](clazz: String, parameterTypes: Array[Class[_]], initargs: Array[Object]): T = {
    newInstance(Class.forName(clazz), parameterTypes, initargs)
  }

  def newInstance[T](clazz: Class[_], parameterTypes: Array[Class[_]], initargs: Array[Object]): T = {
    clazz.getConstructor(parameterTypes: _*).newInstance(initargs).asInstanceOf[T]
  }

  def wrapObject(obj: Object): ClassWrapper = ClassWrapper(obj)

  case class ClassWrapper(var obj: Object) {
    var extMap: util.Map[Class[_], GetValueExtFunction] = new util.HashMap[Class[_], ReflectUtils.GetValueExtFunction](0)

    private def getValue[T](context: util.Map[String, _], names: Array[String], defVal: String, con: String => T): Option[T] = {
      for (key <- names) {
        if (!StrUtils.isEmpty(key)) {
          val ret = context.get(key)
          if (null != ret) {
            return Some(con(ret.toString))
          }
        }
      }
      if (StrUtils.isEmpty(defVal)) {
        None
      } else {
        Some(con(defVal))
      }
    }

    def getAnnotation(field: Field): ConfigValue = {
      field.getAnnotation(classOf[ConfigValue])
    }

    def setObject(objNew: Object): ClassWrapper = {
      obj = objNew
      this
    }

    def putExtFunction(c: Class[_], f: GetValueExtFunction): ClassWrapper = {
      if (null == extMap) {
        extMap = new util.HashMap[Class[_], ReflectUtils.GetValueExtFunction]()
      }
      extMap.put(c, f)
      this
    }

    def getConfigValueList(clazz: Class[_]): util.List[Field] = {
      if (null == clazz) {
        new util.ArrayList[Field]()
      } else {
        val list = new util.ArrayList[Field]()
        list.addAll(getConfigValueList(clazz.getSuperclass))
        list.addAll(util.Arrays.asList(clazz.getDeclaredFields(): _*))
        list.stream().filter(new Predicate[Field] {
          override def test(t: Field): Boolean = null != getAnnotation(t)
        }).collect(Collectors.toList())
      }
    }

    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def setValue(field: Field, value: Option[Any]): ClassWrapper = {
      if (value.isDefined) {
        field.setAccessible(true)
        field.set(obj, value.get)
      }
      this
    }

    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def setValue(field: Field, func: GetValueFunction): ClassWrapper = {
      field.setAccessible(true)
      field.set(obj, func.apply())
      this
    }

    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def getValue[T](field: Field): T = {
      field.setAccessible(true)
      field.get(obj).asInstanceOf[T]
    }

    def injectConfigValue(context: util.Map[String, _]): Unit = injectConfigValue(context, this.extMap)

    def injectConfigValue(context: util.Map[String, _], ext: util.Map[Class[_], GetValueExtFunction]): ClassWrapper = {
      import scala.collection.JavaConversions._
      getConfigValueList(obj.getClass).foreach(field => {
        val cv = getAnnotation(field)
        val sp = cv.sp()
        var names: Array[String] = null
        try {
          names = Array.fill[String](2 + cv.aliases().length)(null)
          names.update(0, field.getName)
          names.update(1, cv.alias())
          System.arraycopy(cv.aliases(), 0, names, 2, names.length - 2)
          field.getType.getSimpleName match {
            case "String" =>
              val tmp4 = getValue(context, names, cv.value(), s => s)
              setValue(field, tmp4)
            case "int" =>
              val tmp1 = getValue(context, names, cv.value(), Integer.parseInt)
              setValue(field, tmp1)
            case "String[]" =>
              val tmp6 = getValue(context, names, cv.value(), StringUtils.deleteWhitespace)
              setValue(field, if (tmp6.isDefined) Some(tmp6.get.split(sp)) else Some(Array[String]()))
            case "boolean" =>
              val tmp3 = getValue(context, names, cv.value(), java.lang.Boolean.parseBoolean)
              setValue(field, tmp3)
            case "long" =>
              val tmp2 = getValue(context, names, cv.value(), java.lang.Long.parseLong)
              setValue(field, tmp2)
            case "doubel" =>
              val tmp5 = getValue(context, names, cv.value(), java.lang.Double.parseDouble)
              setValue(field, tmp5)
            case "float" =>
              val tmp7 = getValue(context, names, cv.value(), java.lang.Float.parseFloat)
              setValue(field, tmp7)
            case _ =>
              if (null != ext) {
                val func = ext.get(field.getType)
                if (null != func) {
                  setValue(field, Some(func.apply(context, names, cv, field)))
                }
              }
          }
        } catch {
          case e: Throwable => LOG.error(names.mkString("|"), e)
        }
      })
      this
    }

  }

  @FunctionalInterface
  trait GetValueFunction {
    def apply[T](): T
  }

  @FunctionalInterface
  trait GetValueExtFunction {
    def apply(context: util.Map[String, _], names: Array[String], configValue: ConfigValue, field: Field): Object
  }

}