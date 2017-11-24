package xdean.annotation.methodRef.bad;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import xdean.annotation.MethodRef;
import xdean.annotation.MethodRef.Type;

@Retention(SOURCE)
@Target(METHOD)
@interface JustClass {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();
}

@Retention(SOURCE)
@Target(METHOD)
@interface JustMethod {
  @MethodRef(type = Type.METHOD)
  String method();
}

@Retention(SOURCE)
@Target(METHOD)
@interface OtherUse {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD, defaultClass = Integer.class)
  String method();
}

@Retention(SOURCE)
@Target(METHOD)
@interface MoreDefine {
  @MethodRef(type = Type.CLASS)
  Class<? extends Number> type();

  @MethodRef(type = Type.METHOD)
  String method();

  @MethodRef(type = Type.METHOD)
  String method2();
}
