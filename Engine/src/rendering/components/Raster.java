package rendering.components;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.utils.Camera;
import rendering.utils.WindowManager;
import bgtools.utils.Buffers;
import bgtools.utils.Matrices;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class in charge of rendering all
 * objects in the scene
 *
 * Created by brendan on 15-07-11.
 */
public class Raster {

  private FloatBuffer viewBuffer = BufferUtils.createFloatBuffer(16);
  private FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
  private Vector4f clipPlane;

  public void setClipPlane(Vector4f clipPlane) {
    this.clipPlane = clipPlane;
  }

  private Map<Integer, List<Renderable>> sceneObjects = new HashMap<>();

  public Raster(WindowManager windowManager) {
    Buffers.put(
        Matrices.createProjectionMatrix(0.1f, 1000f, 70f, windowManager.getWidth(), windowManager.getHeight()),
        projectionBuffer);
    projectionBuffer.flip();
  }

  public void prepare() {
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glClearColor(0x5d / 255.f, 0xbf / 255.f, 0xde / 255.f, 0.0f);
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
  }

  public void render(ShaderProgram shader, Camera camera) {

    if (shader.getUniforms().containsKey(Uniform.PROJECTION)) {
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.PROJECTION),
          false,
          projectionBuffer);

    }

    if (clipPlane != null && shader.getUniforms().containsKey(Uniform.CLIPPING_PLANE)) {
      int loc = shader.getUniforms().get(Uniform.CLIPPING_PLANE);
      GL20.glUniform4f(loc, clipPlane.x, clipPlane.y, clipPlane.z, clipPlane.w);
    }

    if (shader.getUniforms().containsKey(Uniform.VIEWER)) {
      int loc = shader.getUniforms().get(Uniform.VIEWER);
      Vector3f pos = camera.getPosition();
      GL20.glUniform3f(loc, pos.x, pos.y, pos.z);
    }

    if (shader.getUniforms().containsKey(Uniform.TIME)) {
      int loc = shader.getUniforms().get(Uniform.TIME);
      float seconds = (float) (System.nanoTime()/10e8);
      GL20.glUniform2f(loc, seconds, seconds);
    }

    if (shader.getUniforms().containsKey(Uniform.VIEWING)) {
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.VIEWING),
          false,
          camera.getViewingBuffer());
    }

    for (int vaoId: sceneObjects.keySet()) {
      GL30.glBindVertexArray(vaoId);
      shader.getAttributes().values().stream().forEach(GL20::glEnableVertexAttribArray);
      for (Renderable renderable: sceneObjects.get(vaoId)) {
        renderable.draw(shader);
      }
    }
    shader.getAttributes().values().stream().forEach(GL20::glDisableVertexAttribArray);
    GL30.glBindVertexArray(0);
  }

  public void addObject(Renderable obj) {
    if (!sceneObjects.containsKey(obj.getVaoId())) {
      sceneObjects.put(obj.getVaoId(), new LinkedList<>());
    }
    sceneObjects.get(obj.getVaoId()).add(obj);
  }

  public boolean removeObject(Renderable obj) {
    return sceneObjects.containsKey(obj.getVaoId()) && sceneObjects.get(obj.getVaoId()).remove(obj);
  }

}
