package xdean.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef.Type;

public class MethodRefDemo {

  @Documented
  @Retention(SOURCE)
  @Target(METHOD)
  public @interface BeforeCall {
    @MethodRef
    String value();
  }

  @Documented
  @Retention(SOURCE)
  @Target(METHOD)
  public @interface AfterCall {
    @MethodRef(type = Type.CLASS)
    Class<?> type();

    @MethodRef(type = Type.METHOD)
    String method();
  }
}
