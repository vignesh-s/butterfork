package butterfork.internal;

final class FieldResourceBinding {
  private final String id;
  private final String name;
  private final String method;
  private final String type;

  FieldResourceBinding(String id, String name, String method, String type) {
    this.id = id;
    this.name = name;
    this.method = method;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getMethod() {
    return method;
  }

  public String getType() {
    return type;
  }
}
