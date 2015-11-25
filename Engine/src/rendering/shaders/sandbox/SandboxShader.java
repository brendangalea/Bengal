package rendering.shaders.sandbox;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;

import java.util.Arrays;
import java.util.List;

import static rendering.components.Attribute.BANANA;
import static rendering.components.Attribute.POSITION;

/**
 * Class for practicing with shaders
 * Created by Brendan Galea on 11/24/2015.
 */
public class SandboxShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/sandbox/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/sandbox/fragment.glsl";
  private static final List<Attribute> attributeList = Arrays.asList(POSITION, BANANA);

  public SandboxShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {}

}