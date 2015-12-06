package rendering.shaders.skybox;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;
import rendering.components.Uniform;

import java.util.Collections;
import java.util.List;


/**
 * Shader for a sky box texture
 * Created by Brendan Galea on 11/26/2015.
 */
public class SkyboxShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/skybox/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/skybox/fragment.glsl";
  private static final List<Attribute> attributeList = Collections.singletonList(Attribute.POSITION);

  public SkyboxShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {
    super.getUniformLocation(Uniform.PROJECTION);
    super.getUniformLocation(Uniform.VIEWING);
  }

}
