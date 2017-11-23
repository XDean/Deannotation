package xdean.annotation.methodRef.testcase.t1;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

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
  Class<? extends Collection<?>> type();

  @MethodRef(type = Type.METHOD)
  String method();
}