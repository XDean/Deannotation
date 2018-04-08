package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Target(METHOD)
@interface ParentNoValue {

  @Target(TYPE)
  @interface Parent {

  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String value();
}

@Target(METHOD)
@interface ParentValueNotClass {

  @Target(TYPE)
  @interface Parent {
    String value();
  }

  @MethodRef(type = Type.METHOD, parentClass = Parent.class)
  String value();
}