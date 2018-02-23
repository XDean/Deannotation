package xdean.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Aggregation {
  Class<?> template();

  @Documented
  @Retention(RUNTIME)
  @Target(METHOD)
  public @interface Attribute {
    Class<? extends Annotation> type();

    String name() default "value";
  }
}
