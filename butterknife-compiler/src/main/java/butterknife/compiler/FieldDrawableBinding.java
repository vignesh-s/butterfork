package butterknife.compiler;

final class FieldDrawableBinding {
  private final String id;
  private final String name;
  private final String tintAttributeId;

  FieldDrawableBinding(String id, String name, String tintAttributeId) {
    this.id = id;
    this.name = name;
    this.tintAttributeId = tintAttributeId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTintAttributeId() {
    return tintAttributeId;
  }
}
