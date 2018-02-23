package xdean.annotation.aggregation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import xdean.annotation.Aggregation;
import xdean.annotation.Aggregation.Attribute;
import xdean.annotation.aggregation.AggregationTest.C.Template;
import xdean.annotation.handler.AggregationClassLoader;
import xdean.annotation.handler.AggregationReflectHandler;

public class AggregationTest {
  @Test
  public void test() throws Exception {
    AggregationReflectHandler.expand(Use.class);
    A a = Use.class.getAnnotation(A.class);
    assertNotNull(a);
    assertEquals("aa", a.value());
    B b = Use.class.getAnnotation(B.class);
    assertNotNull(b);
    assertEquals(123, b.value());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testClassLoader() throws Exception {
    AggregationClassLoader aggregationLoader = new AggregationClassLoader(this.getClass().getClassLoader());
    Class<?> use = aggregationLoader.loadClass(Use.class.getName());
    Class<? extends Annotation> aClass = (Class<? extends Annotation>) aggregationLoader.loadClass(A.class.getName());
    Class<? extends Annotation> bClass = (Class<? extends Annotation>) aggregationLoader.loadClass(B.class.getName());
    Object a = use.getAnnotation(aClass);
    assertNotNull(a);
    assertEquals("aa", a.getClass().getMethod("value").invoke(a));
    Object b = use.getAnnotation(bClass);
    assertNotNull(b);
    assertEquals(123, b.getClass().getMethod("value").invoke(b));
  }

  @Test
  public void testAttribute() throws Exception {
    AggregationReflectHandler.expand(UseAttribute.class);
    A a = UseAttribute.class.getAnnotation(A.class);
    assertNotNull(a);
    assertEquals("bb", a.value());
    B b = UseAttribute.class.getAnnotation(B.class);
    assertNotNull(b);
    assertEquals(321, b.value());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testAttributeClassLoader() throws Exception {
    AggregationClassLoader aggregationLoader = new AggregationClassLoader(this.getClass().getClassLoader());
    Class<?> use = aggregationLoader.loadClass(UseAttribute.class.getName());
    Class<? extends Annotation> aClass = (Class<? extends Annotation>) aggregationLoader.loadClass(A.class.getName());
    Class<? extends Annotation> bClass = (Class<? extends Annotation>) aggregationLoader.loadClass(B.class.getName());
    Object a = use.getAnnotation(aClass);
    assertNotNull(a);
    assertEquals("bb", a.getClass().getMethod("value").invoke(a));
    Object b = use.getAnnotation(bClass);
    assertNotNull(b);
    assertEquals(321, b.getClass().getMethod("value").invoke(b));
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  public @interface A {
    String value();
  }

  @Retention(RUNTIME)
  @Target(TYPE)
  public @interface B {
    int value();
  }

  @Aggregation(template = Template.class)
  @Retention(RUNTIME)
  @Target(TYPE)
  public @interface C {
    @A("aa")
    @B(123)
    public static class Template {
    }

    @Attribute(type = A.class)
    String a() default "aa";

    @Attribute(type = B.class)
    int b() default 123;
  }

  @C
  public static class Use {

  }

  @C(a = "bb", b = 321)
  public static class UseAttribute {

  }
}