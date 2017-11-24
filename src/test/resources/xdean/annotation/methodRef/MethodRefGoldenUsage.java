package xdean.annotation.methodRef;

@UseClass("java.lang.Integer:parseInt")
interface MethodRefGoldenUsage {
  String METHOD = "isNaN";

  @UseCM(type = Integer.class, method = "intValue")
  void func();

  @UseCM(type = Double.class, method = METHOD)
  void bar();
}
