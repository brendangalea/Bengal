package rendering.shaders.phong;

import rendering.components.Attribute;
import rendering.components.Uniform;
import rendering.shaders.ShaderProgram;

/**
 * Phong Shader
 * Created by Brendan Galea on 11/23/2015.
 */
public class PhongShader extends ShaderProgram {

  private static final String VERTEX_FILE = "src/rendering/shaders/phong/vertex.glsl";
  private static final String FRAGMENT_FILE = "src/rendering/shaders/phong/fragment.glsl";

  public PhongShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected void bindAttributes()  {
    super.bindAttribute(0, Attribute.POSITION);
    super.bindAttribute(1, Attribute.NORMAL);
    super.bindAttribute(3, Attribute.COLOR);
  }

  @Override
  protected void getAllUniformLocations() {
    super.getUniformLocation(Uniform.INVERSE_TRANSFORMATION);
    super.getUniformLocation(Uniform.TRANSFORMATION);
    super.getUniformLocation(Uniform.PROJECTION);
    super.getUniformLocation(Uniform.VIEWING);
  }

}

