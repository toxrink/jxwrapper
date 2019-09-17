package x.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by xw on 2019/8/30.
 */
@Retention(RUNTIME)
@Target({ElementType.FIELD})
public @interface InjectValue {
}
