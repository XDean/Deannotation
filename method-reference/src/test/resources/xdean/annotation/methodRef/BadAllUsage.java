package xdean.annotation.methodRef;

interface BadAllUsage {
  @UseAll("java.lang.Integer.parseInt")
  void a();

  @UseAll("java.lang.Integer:ppparseInt")
  void b();

  @UseAll("java.lang.Integerrr:parseInt")
  void c();
}
