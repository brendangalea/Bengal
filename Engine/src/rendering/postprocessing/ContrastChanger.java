package rendering.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import rendering.components.ShaderProgram;
import rendering.shaders.contrast.ContrastShader;


/**
 *
 * Created by brendan on 2016-05-11.
 */
public class ContrastChanger {

  private ImageRenderer imageRenderer = new ImageRenderer();
  private ContrastShader contrastShader = new ContrastShader();

  public ContrastChanger() {
    contrastShader.init();
  }

  public void renderFbo(int texture) {
    contrastShader.start();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    imageRenderer.renderQuad();
    contrastShader.stop();
  }

  public void cleanUp() {
    contrastShader.cleanUp();
    imageRenderer.cleanUp();
  }

  public ShaderProgram getShader() {
    return contrastShader;
  }
}
