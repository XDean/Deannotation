package xdean.annotation.handler;

import static xdean.jex.util.reflect.AnnotationUtil.addAnnotation;
import static xdean.jex.util.reflect.AnnotationUtil.copyAnnotation;
import static xdean.jex.util.reflect.AnnotationUtil.createAnnotationFromMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;

public class AggregationHandler {
  private static final String AGGREGATION = "xdean.annotation.Aggregation";

  @interface Generated {
  }

  @SuppressWarnings("unchecked")
  public static <T> Class<T> handle(Class<T> clz) {
    if (clz.getName().startsWith("java.") ||
        clz.getName().startsWith("sun.") ||
        clz.getName().equals(AGGREGATION) ||
        clz.isAnnotationPresent(Generated.class)) {
      return clz;
    }
    Class<? extends Annotation> aggrClass = (Class<? extends Annotation>) getAggregationClass(clz);
    return byReflect(clz, aggrClass);
  }

  private static <T> Class<T> byReflect(Class<T> clz, Class<? extends Annotation> aggrClass) {
    Arrays.stream(clz.getAnnotations())
        .filter(a -> a.annotationType().isAnnotationPresent(aggrClass))
        .forEach(a -> {
          Annotation aggr = a.annotationType().getAnnotation(aggrClass);
          Class<?> template = getTemplate(aggr);
          Annotation[] annotations = template.getAnnotations();
          Arrays.stream(annotations).forEach(anno -> addAnnotation(clz, copyAnnotation(anno)));
        });
    addAnnotation(clz, createAnnotationFromMap(Generated.class, Collections.emptyMap()));
    return clz;
  }

  private static <T> Class<?> getAggregationClass(Class<T> clz) {
    try {
      return clz.getClassLoader().loadClass(AGGREGATION);
    } catch (ClassNotFoundException e) {
      throw new Error(e);
    }
  }

  private static Class<?> getTemplate(Annotation aggr) {
    try {
      return (Class<?>) aggr.getClass().getMethod("template").invoke(aggr);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      throw new Error(e);
    }
  }
}
