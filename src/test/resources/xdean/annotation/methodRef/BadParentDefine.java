package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Retention(SOURCE)
@Target(METHOD)
@interface ParentNoValue {

  @Retention(SOURCE)
  @Target(TYPE)
  @interface Parent {

  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String value();
}

@Retention(SOURCE)
@Target(METHOD)
@interface ParentValueNotClass {

  @Retention(SOURCE)
  @Target(TYPE)
  @interface Parent {
    String value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String value();
}