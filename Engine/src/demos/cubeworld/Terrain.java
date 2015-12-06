package demos.cubeworld;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import rendering.components.*;
import rendering.utils.Loader;
import toolbox.utils.Buffers;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

/**
 *
 *
 * Created by Brendan Galea on 11/26/2015.
 */
public class Terrain implements Renderable {

  private static final float SIZE = 800;
  private static final int VERTEX_COUNT = 128;

  private RawModel model;
  private int textureId;


  private Matrix4f transformation = new Matrix4f();

  public Terrain(Vector3f origin, Map<Attribute, Integer> bindings) {
    this.model = generateTerrain(bindings);
    this.textureId = new Loader().loadTexture("res/grass_texture.png");
    transformation.setIdentity();
    transformation.setTranslation(origin);
  }

  @Override
  public int getVaoId() {
    return model.getVaoId();
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
          Buffers.identityMatrix4f());
    }

    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
  }

  public void release() {
    model.release();
    GL11.glDeleteTextures(textureId);
  }


  private RawModel generateTerrain(Map<Attribute, Integer> bindings) {
    int count = VERTEX_COUNT * VERTEX_COUNT;
    FloatBuffer vertices = BufferUtils.createFloatBuffer(count * 3);
    FloatBuffer normals = BufferUtils.createFloatBuffer(count * 3);
    FloatBuffer textureCoords = BufferUtils.createFloatBuffer(count * 2);
    IntBuffer indices = BufferUtils.createIntBuffer(6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT));

    for(int i=0;i<VERTEX_COUNT;i++){
      for(int j=0;j<VERTEX_COUNT;j++){
        vertices.put((float)j/((float)VERTEX_COUNT - 1) * SIZE);
        vertices.put(0);
        vertices.put((float)i/((float)VERTEX_COUNT - 1) * SIZE);
        normals.put(0).put(1).put(0);
//        textureCoords.put((float)j/((float)VERTEX_COUNT - 1)).put((float)i/((float)VERTEX_COUNT - 1));
        textureCoords.put(j).put(i);
      }
    }
    for(int gz=0;gz<VERTEX_COUNT-1;gz++){
      for(int gx=0;gx<VERTEX_COUNT-1;gx++){
        int topLeft = (gz*VERTEX_COUNT)+gx;
        int topRight = topLeft + 1;
        int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
        int bottomRight = bottomLeft + 1;
        indices.put(topLeft).put(bottomLeft).put(topRight).put(topRight).put(bottomLeft).put(bottomRight);
      }
    }

    vertices.flip();
    normals.flip();
    textureCoords.flip();
    indices.flip();

    return new RawModel.Builder()
        .setIndices(indices)
        .addAttribute(Attribute.POSITION, vertices, false)
        .addAttribute(Attribute.NORMAL, normals, false)
        .addAttribute(Attribute.TEXTURE_COORDS, textureCoords, false)
        .build(bindings);
  }

}
