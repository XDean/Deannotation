package xdean.annotation.methodRef;

interface BadEnclosingUsage {
  @UseEnclosing(method = "b")
  void a();
}
