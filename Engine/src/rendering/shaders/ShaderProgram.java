package rendering.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import rendering.components.Attribute;
import rendering.components.Uniform;
import toolbox.utils.Buffers;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Brendan Galea on 11/23/2015.
 */
public abstract class ShaderProgram {

  private int programId;
  private int vertexShaderId;
  private int fragmentShaderId;
  private Map<Attribute, Integer> attributes = new HashMap<>();
  private Map<Uniform, Integer> uniforms = new HashMap<>();

  public ShaderProgram(String vertexFile, String fragmentFile) {
    vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
    fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
    programId = GL20.glCreateProgram();
    GL20.glAttachShader(programId, vertexShaderId);
    GL20.glAttachShader(programId, fragmentShaderId);
    bindAttributes();
    GL20.glLinkProgram(programId);
    GL20.glValidateProgram(programId);
    getAllUniformLocations();
  }

  public Map<Attribute, Integer> getAttributes() {
    return attributes;
  }

  public Map<Uniform, Integer> getUniforms() {
    return uniforms;
  }

  public void start() {
    GL20.glUseProgram(programId);
  }

  public void stop() {
    GL20.glUseProgram(0);
  }

  public void cleanUp() {
    stop();
    GL20.glDetachShader(programId, vertexShaderId);
    GL20.glDetachShader(programId, fragmentShaderId);
    GL20.glDeleteShader(vertexShaderId);
    GL20.glDeleteShader(fragmentShaderId);
    GL20.glDeleteProgram(programId);
  }

  protected abstract void bindAttributes();

  protected abstract void getAllUniformLocations();


  protected void bindAttribute(int attributeId, Attribute attribute) {
    attributes.put(attribute, attributeId);
    GL20.glBindAttribLocation(programId, attributeId, attribute.getName());
  }

  protected int getUniformLocation(Uniform uniform) {
    int uniformId = GL20.glGetUniformLocation(programId, uniform.getName());
    uniforms.put(uniform, uniformId);
    return uniformId;
  }

  protected void loadFloat(int location, float value) {
    GL20.glUniform1f(location, value);
  }

  protected void loadVector(int location, Vector3f vector) {
    GL20.glUniform3f(location, vector.x, vector.y, vector.z);
  }

  protected void loadBoolean(int location, boolean value) {
    GL20.glUniform1f(location, value ? 1 : 0);
  }

  protected void loadMatrix(int location, Matrix4f matrix) {
    FloatBuffer matrixBuffer = Buffers.bufferWithMatrix(matrix);
    GL20.glUniformMatrix4fv(location, false, matrixBuffer);
  }

  private static int loadShader(String file, int type) {
    StringBuilder shaderSource = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        shaderSource.append(line).append("\n");
      }
    } catch (IOException e) {
      System.err.println("Could not read file: " + file);
      e.printStackTrace();
      System.exit(-1);
    }
    int shaderId = GL20.glCreateShader(type);
    GL20.glShaderSource(shaderId, shaderSource);
    GL20.glCompileShader(shaderId);
    if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
      System.out.println(GL20.glGetShaderInfoLog(shaderId, 500));
      System.err.println("Could not compile shader: " + file);
      System.exit(-1);
    }
    return shaderId;
  }
}

