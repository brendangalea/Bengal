package rendering.components;

/**
 * Stores list of attributes and their names to be used by deprecated.shaders and renderable objects
 *
 * Created by Brendan Galea on 11/23/2015.
 */
public enum Attribute {
  POSITION("position", 3, Type.FLOAT),
  NORMAL("normal", 3, Type.FLOAT),
  TEXTURE_COORDS("textureCoords", 2, Type.FLOAT),
  COLOR("color", 3, Type.FLOAT),
  SIZE("size", 1, Type.FLOAT),
  INDEX("index", 1, Type.FLOAT);

  private final String name;
  private final int size;
  private final Type type;

  Attribute(String name, int size, Type type) {
    this.name = name;
    this.size = size;
    this.type = type;
  }

  public int getSize() {
    return size;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public enum Type {FLOAT, INT}

}