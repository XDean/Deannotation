package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Documented
@Retention(SOURCE)
@Target(METHOD)
public @interface AfterCall {
  @MethodRef(type = Type.CLASS)
  Class<?> type();

  @MethodRef(type = Type.METHOD)
  String method();
}