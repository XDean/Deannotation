package xdean.annotation.processor;

/**
 * Since the project doesn't dependent any 3rd library. This class provides some utilities from 3rd library.
 *
 * @author XDean
 *
 */
public class Util {
  public static <T extends Throwable, R> R throwIt(T t) throws T {
    throw t;
  }

  @SuppressWarnings("unchecked")
  public static <T extends Throwable, R> R throwAsUncheck(Throwable t) throws T {
    throw (T) t;
  }

  public static void uncheck(RunnableThrow<?> task) {
    try {
      task.run();
    } catch (Exception t) {
      throwAsUncheck(t);
    }
  }

  public static <T> T uncheck(SupplierThrow<T, ?> task) {
    try {
      return task.get();
    } catch (Exception t) {
      return throwAsUncheck(t);
    }
  }

  public static boolean uncatch(RunnableThrow<?> task) {
    try {
      task.run();
      return true;
    } catch (Exception t) {
      return false;
    }
  }

  public static <T> T uncatch(SupplierThrow<T, ?> task) {
    try {
      return task.get();
    } catch (Exception t) {
      return null;
    }
  }

  @FunctionalInterface
  public interface RunnableThrow<T extends Exception> {
    void run() throws T;
  }

  @FunctionalInterface
  public interface SupplierThrow<V, T extends Exception> {
    V get() throws T;
  }
}
