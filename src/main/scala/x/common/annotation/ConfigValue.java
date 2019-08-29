package x.common.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface ConfigValue {

	/**
	 * 默认值
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 别名
	 * 
	 * @return
	 */
	String alias() default "";

	/**
	 * 字符串转数组分隔符
	 * 
	 * @return
	 */
	String sp() default ",";

	/**
	 * 别名数组
	 * 
	 * @return
	 */
	String[] aliases() default {};
}
