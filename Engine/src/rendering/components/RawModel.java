package rendering.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
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


  }
}
