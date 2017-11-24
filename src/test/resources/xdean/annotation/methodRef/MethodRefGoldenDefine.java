package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Retention(SOURCE)
@Target({ TYPE, METHOD })
@interface UseClass {

  @MethodRef
  String value();

}

@Retention(SOURCE)
@Target({ TYPE, METHOD })
@interface UseCM {

  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();

}