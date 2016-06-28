package butterfork.compiler;

final class FieldBitmapBinding {
  private final String id;
  private final String name;

  FieldBitmapBinding(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
