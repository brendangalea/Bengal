package rendering.components;

/**
 * Stores list of attributes and their names to be used by deprecated.shaders and renderable objects
 *
 * Created by Brendan Galea on 11/23/2015.
 */
public class Attribute {

  public enum Type {BOOL, INT, FLOAT}

  public static Attribute POSITION = new Attribute("position", 3, Type.FLOAT);
  public static Attribute POSITION2F = new Attribute("position", 2, Type.FLOAT);
  public static Attribute NORMAL = new Attribute("normal", 3, Type.FLOAT);
  public static Attribute TEXTURE_COORDS = new Attribute("textureCoords", 2, Type.FLOAT);
  public static Attribute COLOR = new Attribute("color", 3, Type.FLOAT);
  public static Attribute SIZE = new Attribute("size", 1, Type.FLOAT);
  public static Attribute INDEX = new Attribute("index", 1, Type.FLOAT);
  public static Attribute INDICES = new Attribute("INDICES", 1, Type.INT);
  public static Attribute RADIUS = new Attribute("radius", 1, Type.FLOAT);


  private final String name;
  private final int size;
  private final Type type;

  public Attribute(String name, int size, Type type) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Attribute attribute = (Attribute) o;
    return size == attribute.size && name.equals(attribute.name) && type == attribute.type;

  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + size;
    result = 31 * result + type.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Attribute{" +
        "name='" + name + '\'' +
        ", size=" + size +
        ", type=" + type +
        '}';
  }
}