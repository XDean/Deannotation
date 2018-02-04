package xdean.annotation.aggregation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.Test;

import xdean.annotation.Aggregation;

public class AggregationTest {
  @Test
  public void test() throws Exception {
    // AggregationHandler.handle(Use.class);
    A a = Use.class.getAnnotation(A.class);
    assertNotNull(a);
    assertEquals("aa", a.value());
    B b = Use.class.getAnnotation(B.class);
    assertNotNull(b);
    assertEquals(123, b.value());
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