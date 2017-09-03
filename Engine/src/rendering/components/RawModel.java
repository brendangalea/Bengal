package rendering.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * A raw model is a wrapper for a vao object
 * Created by Brendan Galea on 11/23/2015.
 */
public class RawModel {

  private int vaoId;
  private int vertexCount;
  private HashMap<Attribute, Integer> vbos;

  public RawModel(int vaoId, int vertexCount, HashMap<Attribute, Integer> vbos) {
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
    this.vbos = vbos;
  }

  public int getVaoId() { return vaoId; }

  public int getVertexCount() { return vertexCount; }

  public HashMap<Attribute, Integer> getVbos() { return vbos; }

  public void release() {
    GL30.glDeleteVertexArrays(vaoId);
    vbos.values().stream().forEach(GL15::glDeleteBuffers);
  }

  public static class Builder {

    private IntBuffer indices;
    private HashMap<Attribute, FloatBuffer> attributes = new HashMap<>();
    private HashMap<Attribute, Boolean> dynamic = new HashMap<>();
    private HashMap<Attribute, Integer> vbos = new HashMap<>();

    public Builder addAttribute(Attribute attribute, FloatBuffer data, boolean isDynamic) {
      attributes.put(attribute, data);
      dynamic.put(attribute, isDynamic);
      return this;
    }

    public Builder setIndices(IntBuffer indices) {
      this.indices = indices;
      return this;
    }

    public RawModel build(Map<Attribute, Integer> bindings) {

      int vaoId = GL30.glGenVertexArrays();
      GL30.glBindVertexArray(vaoId);

      int count = 0;
      for (HashMap.Entry<Attribute, Integer> entry: bindings.entrySet()) {
        Attribute attribute = entry.getKey();
        int attributeId = entry.getValue();
        if (!attributes.containsKey(attribute)) {
          throw new IllegalArgumentException("Model does not have required shader attribute: " + attribute);
        }
        int vboId = GL15.glGenBuffers();
        vbos.put(attribute, vboId);
        int usage = dynamic.get(attribute) ? GL15.GL_STREAM_DRAW : GL15.GL_STATIC_DRAW;
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attributes.get(attribute), usage);
        GL20.glVertexAttribPointer(attributeId, attribute.getSize(), GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        count = attributes.get(attribute).limit() / attribute.getSize();
      }

      if (indices != null) {
        bindIndicesBuffer(indices);
        count = indices.limit();
      }

      GL30.glBindVertexArray(0);
      return new RawModel(vaoId, count, vbos);
    }


    private void bindIndicesBuffer(IntBuffer indices) {
      int vboId = GL15.glGenBuffers();
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
      GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
      vbos.put(Attribute.INDICES, vboId);
    }


    public void writeToFile(String filepath) throws IOException {

      BufferedWriter file = new BufferedWriter(new FileWriter(filepath));
      FloatBuffer vertices = attributes.get(Attribute.POSITION);
      for (int i = 0; i < vertices.limit(); i+=3) {
        file.write(String.format("v %f %f %f\n", vertices.get(i), vertices.get(i+1), vertices.get(i+2)));
      }

      if (attributes.containsKey(Attribute.NORMAL)) {
        FloatBuffer normals = attributes.get(Attribute.NORMAL);
        for (int i = 0; i < normals.limit(); i+=3) {
          file.write(String.format("vn %f %f %f\n", normals.get(i), normals.get(i+1), normals.get(i+2)));
        }
      }

      if (attributes.containsKey(Attribute.TEXTURE_COORDS)) {
        FloatBuffer textureCoords = attributes.get(Attribute.TEXTURE_COORDS);
        for (int i = 0; i < textureCoords.limit(); i+=2) {
          file.write(String.format("vt %f %f\n", textureCoords.get(i), textureCoords.get(i+1)));
        }
      }

      if (indices != null) {
        for (int i = 0; i < indices.limit(); i+=3) {
          file.write(String.format("f %s %s %s\n",
              writeFaceVertex(indices.get(i) + 1),
              writeFaceVertex(indices.get(i+1) + 1),
              writeFaceVertex(indices.get(i+2) + 1)));
        }
      } else {
        for (int i = 0; i < vertices.limit(); i+=3) {
          file.write(String.format("f %s %s %s\n",
              writeFaceVertex(i + 1),
              writeFaceVertex(i + 2),
              writeFaceVertex(i + 3)));
        }
      }

      file.flush();
      file.close();
    }

    private String writeFaceVertex(int index) {
      if (attributes.containsKey(Attribute.TEXTURE_COORDS) && attributes.containsKey(Attribute.NORMAL))
        return String.format("%d/%d/%d", index, index, index);
      else if (attributes.containsKey(Attribute.TEXTURE_COORDS))
        return String.format("%d/%d", index, index);
      else if (attributes.containsKey(Attribute.NORMAL))
        return String.format("%d//%d", index, index);
      return "" + index;
    }
  }


}
