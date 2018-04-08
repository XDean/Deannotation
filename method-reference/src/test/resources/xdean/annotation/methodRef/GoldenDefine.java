package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;
import java.util.List;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Target(METHOD)
@interface UseAll {
  @MethodRef
  String value();
}

@Target(METHOD)
@interface UseClassAndMethod {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();
}

@Target(METHOD)
@interface UseDefaultClass {
  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method();
}

@Target({ METHOD, TYPE })
@interface UseParentClass {

  @Target({ TYPE, PACKAGE })
  @interface Parent {
    Class<?> value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();
}

@Target(METHOD)
@interface UseEnclosing {
  @MethodRef(type = Type.METHOD, findInEnclosing = true)
  String method();
}

@Target({ METHOD, TYPE })
@interface UseTogether {

  @Target({ TYPE, PACKAGE })
  @interface Parent {
    Class<?> value();
  }

  @MethodRef
  String value();

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String method();

  @MethodRef(type = Type.METHOD, defaultClass = String.class)
  String method2() default "length";

  @MethodRef(type = Type.METHOD, findInEnclosing = true)
  String method3();

  @MethodRef(type = Type.METHOD)
  String method4();

  @MethodRef(type = Type.CLASS, parentClass = Parent.class)
  Class<?> type() default List.class;
}