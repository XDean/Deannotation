package xdean.annotation.handler;

import static xdean.jex.util.reflect.AnnotationUtil.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;

import xdean.annotation.Aggregation;

public class AggregationHandler {
  @interface Generated {
  }

  public static <T> Class<T> handle(Class<T> clz) {
    if (clz.isAnnotationPresent(Generated.class)) {
      return clz;
    }
    return byReflect(clz);
  }

  private static <T> Class<T> byReflect(Class<T> clz) {
    Arrays.stream(clz.getAnnotations())
        .filter(a -> a.annotationType().isAnnotationPresent(Aggregation.class))
        .forEach(a -> {
          Aggregation aggr = a.annotationType().getAnnotation(Aggregation.class);
          Class<?> template = aggr.template();
          Annotation[] annotations = template.getAnnotations();
          Arrays.stream(annotations).forEach(anno -> addAnnotation(clz, copyAnnotation(anno)));
        });
    addAnnotation(clz, createAnnotationFromMap(Generated.class, Collections.emptyMap()));
    return clz;
  }

  // TODO
  @SuppressWarnings("unused")
  private static <T> Class<T> byByteCode(Class<T> clz) {
    return clz;
  }
}
