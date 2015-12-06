package rendering.utils;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import rendering.components.RawModel;
import rendering.components.TextureData;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
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

  public int loadTexture(String filename) {

    try (FileInputStream in = new FileInputStream(filename)) {
      PNGDecoder decoder = new PNGDecoder(in);
      // assuming RGB here but should allow for RGB and RGBA (changing wall.png to RGBA will crash this!)
      ByteBuffer buf = ByteBuffer.allocateDirect(3*decoder.getWidth()*decoder.getHeight());
      decoder.decode(buf, decoder.getWidth()*3, PNGDecoder.Format.RGB);
      buf.flip();

      int textureID = GL11.glGenTextures();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
      GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,GL11.GL_RGB, decoder.getWidth(), decoder.getHeight(), 0,
          GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buf);

      GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_LINEAR_MIPMAP_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_LINEAR_MIPMAP_LINEAR);
      GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.6f);
//      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,GL11. GL_NEAREST);
//      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
      return textureID;
    } catch (IOException e) {
      System.err.println("Failed to load deprecated.texture: " + filename);
      e.printStackTrace();
      System.exit(-1);
    }
    return -1;
  }

  public int loadCubeMap(String[] textureFiles) {
    int textureId = GL11.glGenTextures();
    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textureId);

    for (int i = 0; i < textureFiles.length; i++) {
      TextureData data = decodeTextureFile(textureFiles[i]);
      GL11.glTexImage2D(
          GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
          0,
          GL11.GL_RGBA,
          data.getWidth(),
          data.getHeight(),
          0,
          GL11.GL_RGBA,
          GL11.GL_UNSIGNED_BYTE,
          data.getBuffer());
    }


    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
    GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);


    return textureId;
  }

  private TextureData decodeTextureFile(String fileName) {
    int width = 0;
    int height = 0;
    ByteBuffer buffer = null;
    try {
      FileInputStream in = new FileInputStream(fileName);
      PNGDecoder decoder = new PNGDecoder(in);
      width = decoder.getWidth();
      height = decoder.getHeight();
      buffer = ByteBuffer.allocateDirect(4 * width * height);
      decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
      buffer.flip();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Tried to load texture " + fileName + ", didn't work");
      System.exit(-1);
    }
    return new TextureData(buffer, width, height);
  }

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