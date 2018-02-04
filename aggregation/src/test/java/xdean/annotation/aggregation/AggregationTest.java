package xdean.annotation.aggregation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import xdean.annotation.Aggregation;
import xdean.annotation.handler.AggregationHandler;
import xdean.annotation.handler.AggregationLoader;

public class AggregationTest {
  @Test
  public void test() throws Exception {
    AggregationHandler.handle(Use.class);
    A a = Use.class.getAnnotation(A.class);
    assertNotNull(a);
    assertEquals("aa", a.value());
    B b = Use.class.getAnnotation(B.class);
    assertNotNull(b);
    assertEquals(123, b.value());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testClassLoader() throws Exception {
    AggregationLoader aggregationLoader = new AggregationLoader(this.getClass().getClassLoader());
    Class<?> use = aggregationLoader.loadClass(Use.class.getName());
    Class<? extends Annotation> aClass = (Class<? extends Annotation>) aggregationLoader.loadClass(A.class.getName());
    Class<? extends Annotation> bClass = (Class<? extends Annotation>) aggregationLoader.loadClass(B.class.getName());
    Object a = use.getAnnotation(aClass);
    assertNotNull(a);
    assertEquals("aa", a.getClass().getMethod("value").invoke(a));
    Object b = use.getAnnotation(bClass);
    assertNotNull(b);
    assertEquals(123, b.getClass().getMethod("value").invoke(b));
    aggregationLoader.close();
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

  }

  @A("aa")
  @B(123)
  public static class Template {
  }

  @C
  public static class Use {

  }
}