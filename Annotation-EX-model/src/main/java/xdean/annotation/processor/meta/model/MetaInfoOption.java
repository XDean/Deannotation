package xdean.annotation.processor.meta.model;

public class MetaInfoOption {
  public final boolean inherit;
  public final boolean includeStatic;
  public final boolean holdReference;

  private MetaInfoOption(boolean inherit, boolean includeStatic, boolean holdReference) {
    this.inherit = inherit;
    this.includeStatic = includeStatic;
    this.holdReference = holdReference;
  }

  public static class Builder {

    public static Builder create() {
      return new Builder();
    }

    private boolean inherit;
    private boolean includeStatic;
    private boolean holdReference;

    public Builder inherit(boolean b) {
      inherit = b;
      return this;
    }

    public Builder includeStatic(boolean b) {
      includeStatic = b;
      return this;
    }

    public Builder holdReference(boolean b) {
      holdReference = b;
      return this;
    }

    public MetaInfoOption build() {
      return new MetaInfoOption(inherit, includeStatic, holdReference);
    }
  }
}
