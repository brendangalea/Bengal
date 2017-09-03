package rendering.postprocessing;

/**
 *
 * Created by brendan on 2016-05-11.
 */

import bgtools.utils.Buffers;
import rendering.components.Attribute;
import rendering.components.RawModel;


import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.utils.WindowManager;

public class PostProcessing {

  private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
  private static RawModel quad;
  private static ContrastChanger contrastChanger;
  private static GaussianBlur gaussianBlur;

  public static void init(WindowManager manager) {
    contrastChanger = new ContrastChanger();
    gaussianBlur = new GaussianBlur(GaussianBlur.BLUR, manager, 8);
    quad = new RawModel.Builder()
        .addAttribute(Attribute.POSITION2F, Buffers.bufferWithValues(POSITIONS), false)
        .build(contrastChanger.getShader().getAttributes());
  }

  public static void doPostProcessing(int colourTexture) {
    start();
    gaussianBlur.render(colourTexture);
    contrastChanger.renderFbo(gaussianBlur.getOutputTexture());
    end();
  }

  public static void cleanUp() {
    contrastChanger.cleanUp();
  }

  private static void start() {
    GL30.glBindVertexArray(quad.getVaoId());
    GL20.glEnableVertexAttribArray(0);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
  }

  private static void end() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
  }

}