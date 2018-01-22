package xdean.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
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
    /* CONSTRUCTOR, */
    METHOD,
    PROPERTY,
    ALL
  }

  String name() default "";

  AccessLevel level() default AccessLevel.PRIVATE;

  MetaElement[] elements() default MetaElement.ALL;

  boolean inherit() default false;

  boolean includeStatic() default false;

  boolean holdReference() default false;

  public interface ReflectElement<T extends Member> {
    T reflect();

    Class<?> declaringClass();

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

  public interface FieldMeta extends ReflectElement<Field> {
    default Class<?> type() {
      return reflect().getType();
    }
  }

  public interface ExecutableMeta<T extends Executable> extends ReflectElement<T> {

  }

  public interface MethodMeta extends ExecutableMeta<Method> {

  }

  public interface ReadOnlyPropertyMeta {
    MethodMeta getter();
  }

  public interface PropertyMeta extends ReadOnlyPropertyMeta {
    MethodMeta setter();
  }
}
