package rendering.shaders.vblur;

import rendering.components.Attribute;
import rendering.components.ShaderProgram;
import rendering.components.Uniform;

import java.util.Collections;
import java.util.List;

import static rendering.components.Attribute.*;

/**
 *
 * Created by brendan on 2016-05-11.
 */
public class VerticalBlurShader extends ShaderProgram {
  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/vblur/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/vblur/fragment.glsl";
  private static final List<Attribute> attributeList = Collections.singletonList(POSITION2F);

  public VerticalBlurShader() {
    super(VERTEX_FILE, FRAGMENT_FILE);
  }

  @Override
  protected List<Attribute> getAttributeList() {
    return attributeList;
  }

  @Override
  protected void getAllUniformLocations() {
    super.getUniformLocation(Uniform.RADIUS);
  }
}
