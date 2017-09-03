package rendering.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import rendering.components.Uniform;
import rendering.shaders.hblur.HorizontalBlurShader;
import rendering.shaders.vblur.VerticalBlurShader;
import rendering.utils.WindowManager;

/**
 *
 * Created by brendan on 2016-05-11.
 */
public class GaussianBlur {

  public static final int BLUR = 0;
  public static final int HORIZONTAL_BLUR = 1;
  public static final int VERTICAL_BLUR = 2;

  private int mode;
  private ImageRenderer horizontalRenderer;
  private ImageRenderer verticalRenderer;
  private VerticalBlurShader verticalShader;
  private HorizontalBlurShader horizontalShader;
  private int targetWidth;
  private int targetHeight;

  public GaussianBlur(int blurMode, WindowManager manager, int multiplier) {
    this.mode = blurMode;
    if (blurMode == BLUR || blurMode == HORIZONTAL_BLUR) {
      horizontalRenderer =
          new ImageRenderer(manager.getWidth() / multiplier, manager.getHeight() / multiplier, manager);
      horizontalShader = new HorizontalBlurShader();
      horizontalShader.init();
    }

    if (blurMode == BLUR || blurMode == VERTICAL_BLUR) {
      verticalRenderer =
          new ImageRenderer(manager.getWidth() / multiplier, manager.getHeight() / multiplier, manager);
      verticalShader = new VerticalBlurShader();
      verticalShader.init();
    }
    this.targetWidth = manager.getWidth() / multiplier;
    this.targetHeight = manager.getHeight() / multiplier;
  }

  public void render(int texture) {
    if (mode == BLUR) {
      verticalShader.start();
      GL20.glUniform1f(verticalShader.getUniforms().get(Uniform.RADIUS), targetHeight);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      verticalRenderer.renderQuad();
      verticalShader.stop();
      horizontalShader.start();
      GL20.glUniform1f(horizontalShader.getUniforms().get(Uniform.RADIUS), targetWidth);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, verticalRenderer.getOutputTexture());
      horizontalRenderer.renderQuad();
      horizontalShader.stop();
    } else if (mode == HORIZONTAL_BLUR) {
      horizontalShader.start();
      GL20.glUniform1f(horizontalShader.getUniforms().get(Uniform.RADIUS), targetWidth);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      horizontalRenderer.renderQuad();
      horizontalShader.stop();
    } else if (mode == VERTICAL_BLUR) {
      verticalShader.start();
      GL20.glUniform1f(verticalShader.getUniforms().get(Uniform.RADIUS), targetHeight);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
      verticalRenderer.renderQuad();
      verticalShader.stop();
    }
  }

  public int getOutputTexture() {
    if (horizontalRenderer != null)
      return horizontalRenderer.getOutputTexture();
    return verticalRenderer.getOutputTexture();
  }

  public void cleanUp() {
    if (horizontalShader != null)
      horizontalShader.cleanUp();
    if (verticalShader != null)
      verticalShader.cleanUp();
    if (horizontalRenderer != null)
      horizontalRenderer.cleanUp();
    if (verticalRenderer != null)
      verticalRenderer.cleanUp();
  }
}
