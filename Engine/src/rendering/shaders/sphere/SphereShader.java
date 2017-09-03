package rendering.shaders.sphere;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;
import rendering.components.Uniform;

import java.util.Arrays;
import java.util.List;

import static rendering.components.Attribute.COLOR;
import static rendering.components.Attribute.POSITION;
import static rendering.components.Attribute.RADIUS;

/**
 *
 * Created by brendan on 2016-05-05.
 */
public class SphereShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/sphere/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/sphere/fragment.glsl";
  private static final List<Attribute> attributeList = Arrays.asList(POSITION, RADIUS, COLOR);

  public SphereShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {
    super.getUniformLocation(Uniform.TRANSFORMATION);
    super.getUniformLocation(Uniform.PROJECTION);
    super.getUniformLocation(Uniform.VIEWING);
    super.getUniformLocation(Uniform.VIEWER);
  }
}
