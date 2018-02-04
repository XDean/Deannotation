package xdean.annotation.processor.meta.model;

import xdean.annotation.MetaInfo.FieldMeta;

public class ClassMetaImpl {
  private final Class<?> clz;
  private final MetaInfoOption option;

  public ClassMetaImpl(Class<?> clz, MetaInfoOption option) {
    this.clz = clz;
    this.option = option;
  }

  public FieldMeta field(String name) {
    return new FieldMetaImpl(clz, name, option);
  }
}
