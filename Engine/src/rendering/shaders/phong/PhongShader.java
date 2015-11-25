package rendering.shaders.phong;

import rendering.components.Attribute;
import rendering.components.Uniform;
import rendering.components.ShaderProgram;

import java.util.Arrays;
import java.util.List;

import static rendering.components.Attribute.*;

/**
 * Phong Shader
 * Created by Brendan Galea on 11/23/2015.
 */
public class PhongShader extends ShaderProgram {

  private static final String VERTEX_FILE = "Engine/src/rendering/shaders/phong/vertex.glsl";
  private static final String FRAGMENT_FILE = "Engine/src/rendering/shaders/phong/fragment.glsl";
  private static final List<Attribute> attributeList = Arrays.asList(POSITION, NORMAL, COLOR);

  public PhongShader() {
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

