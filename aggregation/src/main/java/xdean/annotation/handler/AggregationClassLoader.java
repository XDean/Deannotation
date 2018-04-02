package xdean.annotation.handler;

import static xdean.jex.util.lang.ExceptionUtil.uncatch;
import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import xdean.annotation.Aggregation;
import xdean.annotation.Aggregation.Attribute;
import xdean.jex.log.LogFactory;

/**
 * ClassLoader to expand {@link Aggregation}
 *
 * @author XDean
 *
 */
public class AggregationClassLoader extends ClassLoader {
  private final ClassLoader delegate;
  private final ClassLoader wrapWorldClassLoader;
  private final ThreadLocal<Boolean> handling = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };

  public AggregationClassLoader(ClassLoader actual) {
    super(actual);
    this.delegate = actual;
    this.wrapWorldClassLoader = new WrapWorldClassLoader(this);
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> loadClass = findLoadedClass(name);
    if (loadClass != null) {
      return loadClass;
    }
    if (handling.get()) {
      loadClass = delegate.loadClass(name);
    } else {
      handling.set(true);
      Class<?> preLoad = wrapWorldClassLoader.loadClass(name);
      if (preLoad.getClassLoader() == wrapWorldClassLoader) {
        loadClass = expand(preLoad);
      } else {
        loadClass = preLoad;
      }
      handling.set(false);
    }
    return loadClass;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> clz = loadClass(name);
    if (resolve) {
      resolveClass(clz);
    }
    return clz;
  }

  /**
   * @param preLoadClass the class pre-load by {@link #WrapWorldClassLoader}
   */
  @SuppressWarnings("unchecked")
  private <T> Class<T> expand(Class<T> preLoadClass) throws ClassNotFoundException {
    try {
      List<Annotation> aggrList = Arrays.stream(preLoadClass.getAnnotations())
          .filter(a -> a.annotationType().isAnnotationPresent(Aggregation.class))
          .collect(Collectors.toList());
      if (aggrList.isEmpty()) {
        return (Class<T>) this.loadClass(preLoadClass.getName());
      }
      CtClass cc = ClassPool.getDefault().get(preLoadClass.getName());
      if (cc.isFrozen()) {
        cc.defrost();
      }
      ClassFile cf = cc.getClassFile();
      ConstPool constPool = cf.getConstPool();
      AnnotationsAttribute annotationAttr = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
      aggrList.forEach(aggregatedAnnotation -> {
        annotationAttr.removeAnnotation(aggregatedAnnotation.annotationType().getName());
        Class<?> template = aggregatedAnnotation.annotationType().getAnnotation(Aggregation.class).template();
        Arrays.stream(template.getAnnotations())
            .forEach(annotation -> annotationAttr.addAnnotation(processAttribute(constPool, annotation, aggregatedAnnotation)));
      });
      return cc.toClass(this, null);
    } catch (NotFoundException | CannotCompileException e) {
      e.printStackTrace();
    }
    return (Class<T>) this.loadClass(preLoadClass.getName());
  }

  private static MemberValue toMemberValue(ConstPool pool, Object value) {
    Class<? extends Object> type = value.getClass();
    if (type.isArray()) {
      int len = Array.getLength(value);
      List<MemberValue> list = new ArrayList<>(len);
      for (int i = 0; i < len; i++) {
        list.add(toMemberValue(pool, Array.get(value, i)));
      }
      ArrayMemberValue amv = new ArrayMemberValue(pool);
      amv.setValue(list.stream().toArray(MemberValue[]::new));
      return amv;
    } else if (Annotation.class.isAssignableFrom(type)) {
      AnnotationMemberValue amv = new AnnotationMemberValue(pool);
      amv.setValue(toJavassistAnnotation(pool, (Annotation) value));
      return amv;
    } else if (type == Boolean.class) {
      return new BooleanMemberValue((Boolean) value, pool);
    } else if (type == Byte.class) {
      return new ByteMemberValue((Byte) value, pool);
    } else if (type == Character.class) {
      return new CharMemberValue((Character) value, pool);
    } else if (type == Class.class) {
      return new ClassMemberValue(((Class<?>) value).getName(), pool);
    } else if (type == Double.class) {
      return new DoubleMemberValue((Double) value, pool);
    } else if (type.isEnum()) {
      EnumMemberValue emv = new EnumMemberValue(pool);
      emv.setType(type.getName());
      emv.setValue(((Enum<?>) value).name());
      return emv;
    } else if (type == Float.class) {
      return new FloatMemberValue((Float) value, pool);
    } else if (type == Integer.class) {
      return new IntegerMemberValue(pool, (Integer) value);
    } else if (type == Long.class) {
      return new LongMemberValue((Long) value, pool);
    } else if (type == Short.class) {
      return new ShortMemberValue((Short) value, pool);
    } else if (type == String.class) {
      return new StringMemberValue((String) value, pool);
    } else {
      throw new IllegalArgumentException(value + " can't be member value.");
    }
  }

  private static javassist.bytecode.annotation.Annotation toJavassistAnnotation(ConstPool pool, Annotation anno) {
    javassist.bytecode.annotation.Annotation result = new javassist.bytecode.annotation.Annotation(
        anno.annotationType().getName(), pool);
    Stream.of(anno.annotationType().getMethods())
        .filter(m -> m.getDeclaringClass() == anno.annotationType())
        .filter(m -> m.getParameterCount() == 0)
        .filter(m -> !Modifier.isStatic(m.getModifiers()))
        .forEach(m -> uncheck(() -> result.addMemberValue(m.getName(), toMemberValue(pool, m.invoke(anno)))));
    return result;
  }

  private static javassist.bytecode.annotation.Annotation processAttribute(ConstPool pool,
      Annotation anno, Annotation define) {
    Class<? extends Annotation> targetType = anno.annotationType();
    javassist.bytecode.annotation.Annotation result = toJavassistAnnotation(pool, anno);
    for (Method m : define.annotationType().getDeclaredMethods()) {
      Attribute att = m.getAnnotation(Attribute.class);
      if (att == null || att.type() != targetType) {
        continue;
      }
      String name = att.name();
      Method targetMethod = uncatch(() -> targetType.getDeclaredMethod(name));
      if (targetMethod == null) {
        LogFactory.from(AggregationClassLoader.class).warn(String.format("Attribute %s not found in %s", name, targetType));
        continue;
      }
      Object value = uncheck(() -> m.invoke(define));
      result.addMemberValue(name, toMemberValue(pool, value));
    }
    return result;
  }

  @Override
  public URL getResource(String name) {
    return delegate.getResource(name);
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    return delegate.getResources(name);
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    return delegate.getResourceAsStream(name);
  }

  @Override
  public void setDefaultAssertionStatus(boolean enabled) {
    delegate.setDefaultAssertionStatus(enabled);
  }

  @Override
  public void setPackageAssertionStatus(String packageName, boolean enabled) {
    delegate.setPackageAssertionStatus(packageName, enabled);
  }

  @Override
  public void setClassAssertionStatus(String className, boolean enabled) {
    delegate.setClassAssertionStatus(className, enabled);
  }

  @Override
  public void clearAssertionStatus() {
    delegate.clearAssertionStatus();
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}