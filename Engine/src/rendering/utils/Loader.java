package rendering.utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.components.RawModel;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads vao into raw model
 * Created by Brendan Galea on 11/22/2015.
 */
public class Loader {

  private List<Integer> vaos = new LinkedList<>();
  private List<Integer> vbos = new LinkedList<>();
  private List<Integer> textures = new LinkedList<>();

  public void addVbo(int id) {
    vbos.add(id);
  }

  public void addVao(int id) {
    vaos.add(id);
  }

  public RawModel loadToVao(float[] positions, float[] textureCoords, int[] indices) {
    int vaoId = createVao();
    bindIndicesBuffer(storeDataInIntBuffer(indices));
    storeDataInAttributeList(0, 3, storeDataInFloatBuffer(positions));
    storeDataInAttributeList(1, 2, storeDataInFloatBuffer(textureCoords));
    unbindVao();
    return new RawModel(vaoId, indices.length, null);
  }

  public RawModel loadToVao(float[] positions) {
    int vaoId = createVao();
    storeDataInAttributeList(0, 3, storeDataInFloatBuffer(positions));
    unbindVao();
    return new RawModel(vaoId, positions.length / 3, null);
  }
//
//  public RawModel loadToVao(FloatBuffer positions, FloatBuffer textureCoords, IntBuffer indices) {
//    int vaoId = createVao();
//    bindIndicesBuffer(indices);
//    storeDataInAttributeList(0, 3, positions);
//    storeDataInAttributeList(1, 2, textureCoords);
//    unbindVao();
//    return new RawModel(vaoId, indices.limit());
//  }

//  public int loadTexture(String filename) {
//    Texture texture = null;
//    try (FileInputStream res = new FileInputStream("res/" + filename + ".png")) {
//      texture = TextureLoader.getTexture("PNG", res);
//    } catch (IOException e) {
//      System.err.println("Failed to load deprecated.texture: " + filename);
//      e.printStackTrace();
//      System.exit(-1);
//    }
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//    int textureId = texture.getTextureID();
//    textures.add(textureId);
//    return textureId;
//  }


  public void cleanUp() {
    vaos.stream().forEach(GL30::glDeleteVertexArrays);
    vbos.stream().forEach(GL15::glDeleteBuffers);
    textures.stream().forEach(GL11::glDeleteTextures);
  }

  private int createVao() {
    int vaoId = GL30.glGenVertexArrays();
    vaos.add(vaoId);
    GL30.glBindVertexArray(vaoId);
    return vaoId;
  }

  private void storeDataInAttributeList(int attributeNumber, int coordSize, FloatBuffer data) {
    int vboId = GL15.glGenBuffers();
    vbos.add(vboId);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
    GL20.glVertexAttribPointer(attributeNumber, coordSize, GL11.GL_FLOAT, false, 0, 0);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
  }

  private void unbindVao() {
    GL30.glBindVertexArray(0);
  }

  private void bindIndicesBuffer(IntBuffer indices) {
    int vboId = GL15.glGenBuffers();
    vbos.add(vboId);
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
  }

  private IntBuffer storeDataInIntBuffer(int[] data) {
    IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }

  private FloatBuffer storeDataInFloatBuffer(float[] data) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
    buffer.put(data);
    buffer.flip();
    return buffer;
  }
}