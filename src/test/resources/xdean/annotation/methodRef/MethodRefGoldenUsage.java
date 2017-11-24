package xdean.annotation.methodRef;

import xdean.annotation.methodRef.UseParentClass.Parent;

@Parent(MethodRefGoldenUsage.class)
interface MethodRefGoldenUsage {
  String METHOD = "isNaN";

  @UseAll("java.lang.Integer:parseInt")
  void a();

  @UseClassAndMethod(type = Integer.class, method = "intValue")
  void b();

  @UseClassAndMethod(type = Double.class, method = METHOD)
  void c();

  @UseParentClass(method = "a")
  void d();

  @UseDefaultClass(method = "length")
  void e();
}
