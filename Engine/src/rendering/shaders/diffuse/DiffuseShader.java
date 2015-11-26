package rendering.shaders.diffuse;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;
import rendering.components.Uniform;

import java.util.Arrays;
import java.util.List;

import static rendering.components.Attribute.COLOR;
import static rendering.components.Attribute.NORMAL;
import static rendering.components.Attribute.POSITION;

/**
 * Very Simple Shader
 * Created by Brendan Galea on 11/25/2015.
 */
public class DiffuseShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/diffuse/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/diffuse/fragment.glsl";
  private static final List<Attribute> attributeList = Arrays.asList(POSITION, NORMAL);

  public DiffuseShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {
    super.getUniformLocation(Uniform.INVERSE_TRANSFORMATION);
    super.getUniformLocation(Uniform.TRANSFORMATION);
    super.getUniformLocation(Uniform.PROJECTION);
    super.getUniformLocation(Uniform.VIEWING);
  }

}
