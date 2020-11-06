package x.utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}
import java.lang.reflect.Field
import java.util
import java.util.stream.Collectors

import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import x.common.annotation.{ConfigValue, InjectValue}
import x.log.Xlog

/**
  * Created by xw on 2019/8/28.
  */
object ReflectUtils {
  private val LOG = Xlog.getLogger(ReflectUtils.getClass)

  /**
    * 打印包含ConfigValue的属性值
    *
    * @param obj 打印对象
    */
  def printConfigValue(obj: Object): Unit = {
    import scala.jdk.CollectionConverters.ListHasAsScala
    val wrapper = wrapObject(obj)
    val valueList = wrapper.getConfigValueList.asScala
      .map(v => {
        val cv = v.getAnnotation(classOf[ConfigValue])
        var tmpValue: Object = null
        val nameList = new util.ArrayList[String](1 + cv.aliases().length)
        try {
          v.getType.getSimpleName match {
            case "String" | "int" | "long" | "float" | "double" | "short" | "boolean" | "ImmutableList" =>
              tmpValue = wrapper.getValue(v)
            case "String[]" =>
              tmpValue = wrapper.getValue(v).asInstanceOf[Array[String]].mkString(cv.sp())
            case _ =>
              val tmp3: Object = wrapper.getValue(v)
              if (null != tmp3) {
                tmpValue = tmp3.toString
              } else {
                tmpValue = ""
              }
          }
          if (StringUtils.isNotEmpty(cv.alias())) {
            nameList.add(cv.alias())
          }
          for (n <- cv.aliases()) {
            if (StringUtils.isNotEmpty(n)) {
              nameList.add(n)
            }
          }
        } catch {
          case _: Throwable =>
        }
        if (StringUtils.isEmpty(cv.alias()) && cv.aliases().length == 0) {
          String.format("\t%1$s = %2$s\n", v.getName, String.valueOf(tmpValue))
        } else {
          String.format("\t%1$s(%2$s) = %3$s\n", v.getName, StringUtils.join(nameList, ","), String.valueOf(tmpValue))
        }
      })
      .reduce(_ + _)
    LOG.info(obj.getClass.toString + " ConsumerConfig values:\n\n" + valueList)
  }

  /**
    * 获取带有InjectValue注解的指定类型字段
    *
    * @param baseClass 需要查找的类
    * @param dstType   查找的对象
    * @return
    */
  @scala.annotation.tailrec
  def findFieldByType(baseClass: Class[_], dstType: Class[_]): Field = {
    if (null == baseClass || classOf[Object].getName.equals(baseClass.getName)) {
      null
    } else {
      val dstName = dstType.getName
      val result = baseClass.getDeclaredFields.find(f => {
        val getType = f.getType.getName.equals(dstName)
        val getAnno = null != f.getAnnotation(classOf[InjectValue])
        getType && getAnno
      })
      if (result.isDefined) {
        result.get
      } else {
        findFieldByType(baseClass.getSuperclass, dstType)
      }
    }
  }

  /**
    * 调用无参方法
    *
    * @param obj    对象
    * @param method 方法名
    * @tparam T 结果
    * @return
    */
  def invokeValue[T](obj: Object, method: String): T = {
    obj.getClass.getMethod(method).invoke(obj).asInstanceOf[T]
  }

  /**
    * 生成实例
    *
    * @param clazz 类名
    * @tparam T 结果
    * @return
    */
  def loadNewInstance[T](clazz: String): T = {
    newInstance(ReflectUtils.getClass.getClassLoader.loadClass(clazz))
  }

  /**
    * 生成实例
    *
    * @param clazz 类名
    * @tparam T 结果
    * @return
    */
  def newInstance[T](clazz: String): T = newInstance(Class.forName(clazz))

  /**
    * 生成实例
    *
    * @param clazz 类名
    * @tparam T 结果
    * @return
    */
  def newInstance[T](clazz: Class[_]): T = clazz.newInstance().asInstanceOf[T]

  /**
    * 生成实例
    *
    * @param clazz          类名
    * @param parameterTypes 参数类型
    * @param initargs       参数
    * @tparam T 结果
    * @return
    */
  def newInstance[T](clazz: String, parameterTypes: Array[Class[_]], initargs: Array[Object]): T = {
    newInstance(Class.forName(clazz), parameterTypes, initargs)
  }

  /**
    * 生成实例
    *
    * @param clazz          类名
    * @param parameterTypes 参数类型
    * @param initargs       参数
    * @tparam T 结果
    * @return
    */
  def newInstance[T](clazz: Class[_], parameterTypes: Array[Class[_]], initargs: Array[Object]): T = {
    clazz.getConstructor(parameterTypes: _*).newInstance(initargs: _*).asInstanceOf[T]
  }

  /**
    * 对象序列化
    * @param obj 序列化对象
    * @return
    */
  def serializeObject(obj: Object): Array[Byte] = {
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(obj)
    val ret = byteArrayOutputStream.toByteArray()
    IOUtils.closeQuietly(objectOutputStream)
    IOUtils.closeQuietly(byteArrayOutputStream)
    ret
  }

  /**
    * 对象方序列化
    * @param data 数据
    * @tparam T 对象类
    * @return
    */
  def deserializeObject[T](data: Array[Byte]): T = {
    val in = new ByteArrayInputStream(data)
    val oin = new ObjectInputStream(in)
    val ret = oin.readObject()
    IOUtils.closeQuietly(oin)
    IOUtils.closeQuietly(in)
    ret.asInstanceOf[T]
  }

  /**
    * 字段赋值
    *
    * @param obj   对象
    * @param field 字段
    * @param value 设置值
    */
  def setValue(obj: Object, field: Field, value: Any): Unit = {
    field.setAccessible(true)
    field.set(obj, value)
  }

  /**
    * 生成类包装器
    *
    * @param obj 对象
    * @return
    */
  def wrapObject(obj: Object): ClassWrapper = ClassWrapper(obj)

  case class ClassWrapper(var obj: Object) {
    private var extMap: util.Map[Class[_], GetValueExtFunction] =
      new util.HashMap[Class[_], ReflectUtils.GetValueExtFunction](0)

    private def getValue[T](
        context: util.Map[String, _],
        names: Array[String],
        defVal: String,
        con: String => T
    ): Option[T] = {
      for (key <- names) {
        if (!StrUtils.isEmpty(key)) {
          val ret = context.get(key)
          if (null != ret) {
            return Some(con(StrUtils.getEnvironmentValue(ret.toString)))
          }
        }
      }
      if (StrUtils.isEmpty(defVal)) {
        None
      } else {
        Some(con(defVal))
      }
    }

    /**
      * 获取字段注解
      *
      * @param field 字段
      * @return
      */
    def getAnnotation(field: Field): ConfigValue = {
      field.getAnnotation(classOf[ConfigValue])
    }

    /**
      * 设置新的包装类
      *
      * @param objNew 包装对象
      * @return
      */
    def setObject(objNew: Object): ClassWrapper = {
      obj = objNew
      this
    }

    /**
      * 添加额外的赋值方法
      *
      * @param c 类型
      * @param f 方法
      * @return
      */
    def putExtFunction(c: Class[_], f: GetValueExtFunction): ClassWrapper = {
      if (null == extMap) {
        extMap = new util.HashMap[Class[_], ReflectUtils.GetValueExtFunction]()
      }
      extMap.put(c, f)
      this
    }

    /**
      * 获取有ConfigValue注解的字段
      *
      * @param clazz 类型
      * @return
      */
    def getConfigValueList(clazz: Class[_]): util.List[Field] = {
      if (null == clazz) {
        new util.ArrayList[Field]()
      } else {
        val list = new util.ArrayList[Field]()
        list.addAll(getConfigValueList(clazz.getSuperclass))
        list.addAll(util.Arrays.asList(clazz.getDeclaredFields(): _*))
        list
          .stream()
          .filter(t => null != getAnnotation(t))
          .collect(Collectors.toList())
      }
    }

    /**
      * 获取有ConfigValue注解的字段
      *
      * @return
      */
    def getConfigValueList: util.List[Field] = {
      val clazz = obj.getClass
      if (null == clazz) {
        new util.ArrayList[Field]()
      } else {
        val list = new util.ArrayList[Field]()
        list.addAll(getConfigValueList(clazz.getSuperclass))
        list.addAll(util.Arrays.asList(clazz.getDeclaredFields(): _*))
        list
          .stream()
          .filter(t => null != getAnnotation(t))
          .collect(Collectors.toList())
      }
    }

    /**
      * 字段设值
      *
      * @param field 字段
      * @param value 设置值
      * @return
      */
    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def setValue(field: Field, value: Option[Any]): ClassWrapper = {
      if (value.isDefined) {
        ReflectUtils.setValue(obj, field, value.get)
      }
      this
    }

    /**
      * 字段设值
      *
      * @param field 字段
      * @param func  方法
      * @return
      */
    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def setValue(field: Field, func: GetValueFunction): ClassWrapper = {
      ReflectUtils.setValue(obj, field, func.apply())
      this
    }

    /**
      * 获取字段值
      *
      * @param field 字段
      * @tparam T 结果
      * @return
      */
    @throws[IllegalArgumentException]
    @throws[IllegalAccessException]
    def getValue[T](field: Field): T = {
      field.setAccessible(true)
      field.get(obj).asInstanceOf[T]
    }

    /**
      * 对象属性设值
      *
      * @param context 配置
      */
    def injectConfigValue(context: util.Map[String, _]): Unit = injectConfigValue(context, this.extMap)

    /**
      * 对象属性设值
      *
      * @param context 配置
      * @param ext     额外配置获取方法
      * @return
      */
    def injectConfigValue(context: util.Map[String, _], ext: util.Map[Class[_], GetValueExtFunction]): ClassWrapper = {
      import scala.jdk.CollectionConverters.ListHasAsScala
      getConfigValueList(obj.getClass).asScala.foreach(field => {
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

  /**
    * 取值
    */
  @FunctionalInterface
  trait GetValueFunction {
    def apply[T](): T
  }

  /**
    * 取值
    */
  @FunctionalInterface
  trait GetValueExtFunction {
    def apply(context: util.Map[String, _], names: Array[String], configValue: ConfigValue, field: Field): Object
  }

}
