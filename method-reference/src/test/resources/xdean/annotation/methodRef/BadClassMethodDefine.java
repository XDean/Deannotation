package xdean.annotation.methodRef;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Target(METHOD)
@interface JustClass {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();
}

@Target(METHOD)
@interface JustMethod {
  @MethodRef(type = Type.METHOD)
  String method();
}

@Target(METHOD)
@interface OtherUse {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD, defaultClass = Integer.class)
  String method();
}

@Target(METHOD)
@interface MoreDefine {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();

  @MethodRef(type = Type.METHOD)
  String method2();
}

@Target(METHOD)
@interface DuplicateDefine {
  @MethodRef(type = Type.METHOD)
  String method();

  @MethodRef(type = Type.METHOD)
  String method2();
}
