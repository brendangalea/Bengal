package demos.cubeworld;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import rendering.components.Attribute;
import rendering.components.RawModel;
import rendering.components.Renderable;
import rendering.components.ShaderProgram;
import rendering.utils.Loader;
import bgtools.utils.Buffers;

import java.util.Map;

/**
 *
 * Created by Brendan Galea on 11/26/2015.
 */
public class Skybox implements Renderable {

  private static final float SIZE = 400f;

  private static final float[] VERTICES = {
      -SIZE,  SIZE, -SIZE,
      -SIZE, -SIZE, -SIZE,
      SIZE, -SIZE, -SIZE,
      SIZE, -SIZE, -SIZE,
      SIZE,  SIZE, -SIZE,
      -SIZE,  SIZE, -SIZE,

      -SIZE, -SIZE,  SIZE,
      -SIZE, -SIZE, -SIZE,
      -SIZE,  SIZE, -SIZE,
      -SIZE,  SIZE, -SIZE,
      -SIZE,  SIZE,  SIZE,
      -SIZE, -SIZE,  SIZE,

      SIZE, -SIZE, -SIZE,
      SIZE, -SIZE,  SIZE,
      SIZE,  SIZE,  SIZE,
      SIZE,  SIZE,  SIZE,
      SIZE,  SIZE, -SIZE,
      SIZE, -SIZE, -SIZE,

      -SIZE, -SIZE,  SIZE,
      -SIZE,  SIZE,  SIZE,
      SIZE,  SIZE,  SIZE,
      SIZE,  SIZE,  SIZE,
      SIZE, -SIZE,  SIZE,
      -SIZE, -SIZE,  SIZE,

      -SIZE,  SIZE, -SIZE,
      SIZE,  SIZE, -SIZE,
      SIZE,  SIZE,  SIZE,
      SIZE,  SIZE,  SIZE,
      -SIZE,  SIZE,  SIZE,
      -SIZE,  SIZE, -SIZE,

      -SIZE, -SIZE, -SIZE,
      -SIZE, -SIZE,  SIZE,
      SIZE, -SIZE, -SIZE,
      SIZE, -SIZE, -SIZE,
      -SIZE, -SIZE,  SIZE,
      SIZE, -SIZE,  SIZE
  };

  private static String[] TEXTURE_FILES = {
      "res/cloudy_skybox/right.png",
      "res/cloudy_skybox/left.png",
      "res/cloudy_skybox/top.png",
      "res/cloudy_skybox/bottom.png",
      "res/cloudy_skybox/back.png",
      "res/cloudy_skybox/front.png"
  };

  private RawModel model;
  private int textureId;

  public Skybox(Map<Attribute, Integer> bindings) {
    model = new RawModel.Builder()
        .addAttribute(Attribute.POSITION, Buffers.bufferWithValues(VERTICES), false)
        .build(bindings);
    textureId = new Loader().loadCubeMap(TEXTURE_FILES);
  }

  @Override
  public int getVaoId() {
    return model.getVaoId();
  }

  @Override
  public void draw(ShaderProgram shader) {
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
  }

  public void release() {
    model.release();
    GL11.glDeleteTextures(textureId);
  }
}
