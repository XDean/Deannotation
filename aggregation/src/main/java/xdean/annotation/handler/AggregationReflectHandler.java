package xdean.annotation.handler;

import static xdean.jex.util.reflect.AnnotationUtil.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;

import xdean.annotation.Aggregation;

public class AggregationReflectHandler {
  @interface Generated {
  }

  public static <T> Class<T> expand(Class<T> clz) {
    if (clz.isAnnotationPresent(Generated.class)) {
      return clz;
    }
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
}
