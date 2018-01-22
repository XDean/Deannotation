package xdean.annotation.processor.meta.model;

import static xdean.annotation.processor.Util.uncheck;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import xdean.annotation.MetaInfo.FieldMeta;

public class FieldMetaImpl implements FieldMeta {

  private final Class<?> declaringClass;
  private final String name;
  private final Class<?> type;
  private final Supplier<Field> field;

  public FieldMetaImpl(Class<?> clz, String name, MetaInfoOption option) {
    this.declaringClass = clz;
    this.name = name;
    Supplier<Field> s = () -> uncheck(() -> clz.getDeclaredField(name));
    Field f = s.get();
    this.type = f.getType();
    this.field = option.holdReference ? () -> f : s;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Class<?> type() {
    return type;
  }

  @Override
  public Field reflect() {
    return field.get();
  }

  @Override
  public Class<?> declaringClass() {
    return declaringClass;
  }
}
