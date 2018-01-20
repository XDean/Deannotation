package xdean.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

@Retention(SOURCE)
@Target(TYPE)
public @interface MetaInfo {
  public enum AccessLevel {
    PUBLIC,
    /* MODULE, */
    PROTECTED,
    PACKAGE,
    PRIVATE
  }

  public enum MetaElement {
    FIELD,
    CONSTRUCTOR,
    METHOD,
    PROPERTY
  }

  @Retention(SOURCE)
  public @interface Element {
    MetaElement value();

    AccessLevel level();
  }

  String name() default "";

  Element[] elements() default {

  };

  public interface ReflectElement<T extends Member> {
    T reflect();

    default String name() {
      return reflect().getName();
    }

    default int modifier() {
      return reflect().getModifiers();
    }

    default boolean isSynthetic() {
      return reflect().isSynthetic();
    }
  }

  public interface FieldInfo extends ReflectElement<Field> {

  }

  public interface ExecutableInfo<T extends Executable> extends ReflectElement<T> {

  }

  public interface MethodInfo extends ExecutableInfo<Method> {

  }

  public interface ConstructorInfo<T> extends ExecutableInfo<Constructor<T>> {

  }

  public interface ReadOnlyPropertyInfo {
    MethodInfo getter();
  }

  public interface PropertyInfo extends ReadOnlyPropertyInfo {
    MethodInfo setter();
  }
}
