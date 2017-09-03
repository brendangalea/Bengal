package rendering.shaders.contrast;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;

import java.util.Collections;
import java.util.List;

import static rendering.components.Attribute.*;

/**
 * Post processing effect shader to change contrast
 * Created by brendan on 2016-05-11.
 */
public class ContrastShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/contrast/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/contrast/fragment.glsl";
  private static final List<Attribute> attributeList = Collections.singletonList(POSITION2F);

  public ContrastShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {}

}
