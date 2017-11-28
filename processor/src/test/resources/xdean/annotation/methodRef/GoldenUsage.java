package xdean.annotation.methodRef;

import java.util.Map;

import xdean.annotation.methodRef.UseParentClass.Parent;

@Parent(GoldenUsage.class)
@xdean.annotation.methodRef.UseTogether.Parent(Map.class)
interface GoldenUsage {
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

  @UseTogether(value = "java.lang.String:length", method = "put", method3 = "subList")
  void f();

  @UseTogether(value = "java.lang.String:length", method = "put", type = void.class, method3 = "get")
  void g();
}
