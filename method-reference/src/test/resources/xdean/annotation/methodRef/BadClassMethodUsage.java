package xdean.annotation.methodRef;

interface BadClassMethodUsage {
  @UseClassAndMethod(type = Integer.class, method = "valueOfff")
  void a();
}
