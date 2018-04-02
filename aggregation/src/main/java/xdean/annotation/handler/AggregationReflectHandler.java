package xdean.annotation.handler;

import static xdean.jex.util.lang.ExceptionUtil.uncatch;
import static xdean.jex.util.lang.ExceptionUtil.uncheck;
import static xdean.jex.util.reflect.AnnotationUtil.addAnnotation;
import static xdean.jex.util.reflect.AnnotationUtil.changeAnnotationValue;
import static xdean.jex.util.reflect.AnnotationUtil.copyAnnotation;
import static xdean.jex.util.reflect.AnnotationUtil.createAnnotationFromMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import xdean.annotation.Aggregation;
import xdean.annotation.Aggregation.Attribute;
import xdean.jex.log.Logable;

public class AggregationReflectHandler {
  @interface Processed {
  }

  public static <T> Class<T> expand(Class<T> clz) {
    return new Handler<>(clz).getResult();
  }

  private static class Handler<T> implements Logable {
    Class<T> clz;

    Handler(Class<T> clz) {
      this.clz = clz;
    }

    Class<T> getResult() {
      if (clz.isAnnotationPresent(Processed.class)) {
        return clz;
      }
      Arrays.stream(clz.getAnnotations())
          .filter(a -> a.annotationType().isAnnotationPresent(Aggregation.class))
          .forEach(a -> processAnnotation(a));
      processedMarker();
      return clz;
    }

    void processAnnotation(Annotation a) {
      Aggregation aggr = a.annotationType().getAnnotation(Aggregation.class);
      Class<?> template = aggr.template();
      Annotation[] annotations = template.getAnnotations();
      Arrays.stream(annotations).forEach(anno -> addAnnotation(clz, processAttribute(a, anno)));
    }

    Annotation processAttribute(Annotation define, Annotation target) {
      Class<? extends Annotation> defineType = define.annotationType();
      Class<? extends Annotation> targetType = target.annotationType();
      Annotation result = copyAnnotation(target);
      for (Method m : defineType.getDeclaredMethods()) {
        Attribute att = m.getAnnotation(Attribute.class);
        if (att == null || att.type() != targetType) {
          continue;
        }
        String name = att.name();
        Method targetMethod = uncatch(() -> targetType.getDeclaredMethod(name));
        if (targetMethod == null) {
          warn(String.format("Attribute %s not found in %s", name, targetType));
          continue;
        }
        Object value = uncheck(() -> m.invoke(define));
        changeAnnotationValue(result, name, value);
      }
      return result;
    }

    void processedMarker() {
      addAnnotation(clz, createAnnotationFromMap(Processed.class, Collections.emptyMap()));
    }
  }
}
