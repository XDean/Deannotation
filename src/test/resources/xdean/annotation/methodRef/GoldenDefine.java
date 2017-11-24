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
@Target({ METHOD, TYPE })
@interface UseParentClass {
  @Retention(SOURCE)
  @Target({ TYPE, PACKAGE })
  @interface Parent {
    Class<?> value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();
}

@Retention(SOURCE)
@Target({ METHOD, TYPE })
@interface UseTogether {
  @Retention(SOURCE)
  @Target({ TYPE, PACKAGE })
  @interface Parent {
    Class<?> value();
  }

  @MethodRef
  String value();

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();

  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method2();

  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method3();
}