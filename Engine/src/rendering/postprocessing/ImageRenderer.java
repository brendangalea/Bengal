package rendering.postprocessing;

import org.lwjgl.opengl.GL11;
import rendering.utils.WindowManager;

/** Renders a quad covering the display either to an fbo or the screen */
public class ImageRenderer {

  private Fbo fbo;

  public ImageRenderer(int width, int height, WindowManager windowManager) {
    this.fbo = new Fbo(width, height, Fbo.NONE, windowManager);
  }

  /* Renders to screen rather then fbo */
  public ImageRenderer() {}

  public void renderQuad() {
    if (fbo != null) {
      fbo.bindFrameBuffer();
    }
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
    if (fbo != null) {
      fbo.unbindFrameBuffer();
    }
  }

  public int getOutputTexture() {
    return fbo.getColourTexture();
  }

  public void cleanUp() {
    if (fbo != null) {
      fbo.cleanUp();
    }
  }

}