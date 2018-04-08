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

  @UseEnclosing(method = "e")
  void f();

  @UseTogether(value = "java.lang.String:length", method = "put", method3 = "e", method4 = "subList")
  void g();

  @UseTogether(value = "java.lang.String:length", method = "put", method3 = "f", type = void.class, method4 = "get")
  void h();
}
