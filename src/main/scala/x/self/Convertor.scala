package x.self

/**
  * Created by xw on 2019/8/28.
  */
@FunctionalInterface
trait Convertor[T] {
  def apply[T](s: String): T
}
