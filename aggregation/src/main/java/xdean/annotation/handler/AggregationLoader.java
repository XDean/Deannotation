package xdean.annotation.handler;

import java.net.URL;
import java.net.URLClassLoader;

public class AggregationLoader extends URLClassLoader {

  public AggregationLoader(ClassLoader parent) {
    super(getUrls(parent), parent.getParent());
  }

  private static URL[] getUrls(ClassLoader cl) {
    return ((URLClassLoader) cl).getURLs();
  }

  @Override
  public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    return AggregationHandler.handle(super.loadClass(name, resolve));
  }
}