package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Retention(SOURCE)
@Target(METHOD)
@interface UseAll {
  @MethodRef
  String value();
}

@Retention(SOURCE)
@Target(METHOD)
@interface UseClassAndMethod {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();
}

@Retention(SOURCE)
@Target(METHOD)
@interface UseDefaultClass {
  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method();
}

@Retention(SOURCE)
@Target(METHOD)
@interface UseParentClass {
  @Retention(SOURCE)
  @Target(TYPE)
  @interface Parent {
    Class<?> value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();
}