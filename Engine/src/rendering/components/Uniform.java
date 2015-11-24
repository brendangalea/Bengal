package rendering.components;


/**
 * List of uniforms used by deprecated.shaders and renderable objects
 *
 * Created by brendangalea on 7/26/15.
 */
public enum Uniform {
  VIEWING("viewing"),
  TRANSFORMATION("transformation"),
  PROJECTION("projection"),
  INVERSE_TRANSFORMATION("invTransform"),
  COLOR("color"),
  HEIGHT("height"),
  CLIPPING_PLANE("clippingPlane"),
  REFLECTION_TEXTURE("reflectionTexture"),
  REFRACTION_TEXTURE("refractionTexture"),
  DUDV_MAP("dudvMap"),
  NORMAL_MAP("normalMap"),
  TIME("seconds"),
  VIEWER("viewer"),
  RADIUS("radius"),
  MODELVIEW("modelview"),
  LIGHT0("light0");

  public final String name;

  Uniform(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}