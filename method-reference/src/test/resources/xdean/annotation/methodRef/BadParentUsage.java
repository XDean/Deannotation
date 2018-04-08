package xdean.annotation.methodRef;

import java.util.List;

import xdean.annotation.methodRef.UseParentClass.Parent;

interface NoParent {
  @UseParentClass(method = "add")
  void a();
}

@Parent(List.class)
interface MethodNotFound {
  @UseParentClass(method = "addd")
  void a();
}

class Enclose {
  @UseParentClass(method = "toString")
  interface EncloseNoParent {

  }
}