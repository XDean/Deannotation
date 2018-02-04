package xdean.annotation.meta;

import java.util.List;

import xdean.annotation.MetaInfo;

@MetaInfo(

)
public class Golden {

  public static final String GLOBAL = "123";

  private int id;
  private String name;
  private List<Golden> children;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Golden> getChildren() {
    return children;
  }

  public void setChildren(List<Golden> children) {
    this.children = children;
  }

  public static String getGlobal() {
    return GLOBAL;
  }
}
