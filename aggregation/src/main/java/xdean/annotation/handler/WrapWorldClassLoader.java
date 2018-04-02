package xdean.annotation.handler;

import static xdean.jex.util.lang.ExceptionUtil.uncheck;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import xdean.annotation.Aggregation;
import xdean.jex.log.Logable;

/**
 * This class loader load class in wrap world which will never has effect to the application. If a
 * class need expand the aggregation annotations, this loader will load it to give a template to do
 * bytecode changes.
 *
 * @author XDean
 *
 */
class WrapWorldClassLoader extends ClassLoader implements Logable {
  public WrapWorldClassLoader(ClassLoader parent) {
    super(parent);
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> clz = findLoadedClass(name);
    if (clz != null) {
      return clz;
    }
    try {
      if (!name.startsWith("java.lang")) {
        CtClass cc = ClassPool.getDefault().get(name);
        if (hasAggregation(cc)) {
          clz = cc.toClass(this, null);
          debug("Load in wrap world: " + name);
        }
      }
    } catch (NotFoundException e) {
      debug("Wrap world can't find " + name);
    } catch (CannotCompileException e) {
      debug("Wrap world can't compile " + name);
    }
    if (clz == null) {
      clz = getParent().loadClass(name);
    }
    if (resolve) {
      resolveClass(clz);
    }
    return clz;
  }

  private boolean hasAggregation(CtClass cc) {
    ClassFile cf = cc.getClassFile();
    AnnotationsAttribute annoInfo = (AnnotationsAttribute) cf.getAttribute(AnnotationsAttribute.visibleTag);
    if (annoInfo == null) {
      return false;
    }
    return Arrays.stream(annoInfo.getAnnotations())
        .map(a -> a.getTypeName())
        .map(n -> uncheck(() -> ClassPool.getDefault().get(n)))
        .map(c -> (AnnotationsAttribute) c.getClassFile().getAttribute(AnnotationsAttribute.visibleTag))
        .anyMatch(aa -> aa.getAnnotation(Aggregation.class.getName()) != null);
  }
}
