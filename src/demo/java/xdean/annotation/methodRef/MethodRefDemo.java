package xdean.annotation.methodRef;

import java.util.HashMap;

public class MethodRefDemo {

  @BeforeCall("java.lang.System:currentTimeMillis")
  public static void func() {

  }

  @AfterCall(type = HashMap.class, method = "size")
  public void bar() {

  }
}
