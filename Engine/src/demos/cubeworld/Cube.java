package demos.cubeworld;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.components.*;
import rendering.utils.Loader;
import toolbox.utils.Buffers;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;


/**
 * Renderable Cube Model
 * Created by Brendan Galea on 11/25/2015.
 */
public class Cube implements Renderable {

  private RawModel rawModel;

  private Matrix4f transformation;

  private static final float[] vertices = {
      -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
      -0.5f, 0.5f, -0.5f,-0.5f, -0.5f, -0.5f,0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
      0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f,
      -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
      0.5f, 0.5f, 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f, -0.5f,
      0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, -0.5f,
  };

  private static final int[] indices = {
      0, 1, 3, 3, 1, 2,
      4, 7, 5, 7, 6, 5,
      8, 9, 10, 9, 11, 10,
      12, 13, 14, 13, 14, 15,
      16, 17, 18, 17, 18, 19,
  };

  private static final float[] uvs = {
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
      0.f, 0.f, 0.f, 1.f, 1.f, 1.f, 1.f, 0.f,
  };

  private static final float[] normals = {
      0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
      0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
      1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
      -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
      0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
      0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0
  };

  public Cube(Map< Attribute, Integer> bindings) {

    rawModel = new RawModel.Builder()
        .addAttribute(Attribute.POSITION, Buffers.bufferWithValues(vertices), false)
        .addAttribute(Attribute.NORMAL, Buffers.bufferWithValues(normals), false)
        .addAttribute(Attribute.TEXTURE_COORDS, Buffers.bufferWithValues(uvs), false)
        .setIndices(Buffers.bufferWithValues(indices))
        .build(bindings);

//    rawModel = new Loader().loadToVao(vertices, uvs, indices);
    transformation = new Matrix4f();
    transformation.setIdentity();
  }


  @Override
  public void draw(ShaderProgram shader) {

    Map<Uniform, Integer> uniforms = shader.getUniforms();

    if (uniforms.containsKey(Uniform.TRANSFORMATION)) {
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.TRANSFORMATION),
          false,
          Buffers.bufferWithMatrix(transformation));
    }

    if (uniforms.containsKey(Uniform.INVERSE_TRANSFORMATION)) {
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.INVERSE_TRANSFORMATION),
          false,
          Buffers.bufferWithMatrix(transformation));
    }


    GL30.glBindVertexArray(rawModel.getVaoId());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
//    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    GL20.glDisableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL30.glBindVertexArray(0);

  }

  @Override
  public int getVaoId() {
    return rawModel.getVaoId();
  }


}
