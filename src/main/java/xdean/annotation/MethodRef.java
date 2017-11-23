package xdean.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(METHOD)
public @interface MethodRef {

  public enum Type {
    ALL, CLASS, METHOD
  }

  Type type() default Type.ALL;

  char splitor() default ':';

}
