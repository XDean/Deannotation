package xdean.annotation.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public class AggregationLoader extends ClassLoader {
  private final ClassLoader delegate;
  private final ThreadLocal<Boolean> handling = new ThreadLocal<Boolean>() {
    @Override
    protected Boolean initialValue() {
      return false;
    }
  };

  public AggregationLoader(ClassLoader actual) {
    super(actual);
    this.delegate = actual;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> loadClass = delegate.loadClass(name);
    if (handling.get() == false) {
      handling.set(true);
      AggregationHandler.handle(loadClass);
      handling.set(false);
    }
    return loadClass;
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