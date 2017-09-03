package rendering.components;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * Created by Brendan Galea on 11/23/2015.
 */
public abstract class ShaderProgram {

  private int programId = -1;
  private int vertexShaderId;
  private int fragmentShaderId;
  private Map<Attribute, Integer> attributes = new HashMap<>();
  private Map<Uniform, Integer> uniforms = new HashMap<>();

  public ShaderProgram(String vertexFile, String fragmentFile) {
    vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
    fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
  }

  public Map<Attribute, Integer> getAttributes() {
    return attributes;
  }

  public Map<Uniform, Integer> getUniforms() {
    return uniforms;
  }

  public void init() {
    init(bindingsForSet(this));
  }
  public void init(Map<Attribute, Integer> bindings) {
    programId = GL20.glCreateProgram();
    GL20.glAttachShader(programId, vertexShaderId);
    GL20.glAttachShader(programId, fragmentShaderId);
    bindAttributes(bindings);
    GL20.glLinkProgram(programId);
    GL20.glValidateProgram(programId);
    getAllUniformLocations();
  }

  public void start() {
//    if (programId == -1) {
//      init();
//    }
    if (programId == -1) {
      throw new RuntimeException("Shader has not been initialized");
    }
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

  public static Map<Attribute, Integer> bindingsForSet(ShaderProgram... shaders) {
    Map<Attribute, Integer> bindings = new HashMap<>();
    Set<String> names = new HashSet<>();
    int location = 0;
    for (ShaderProgram shader: shaders) {
      for (Attribute attribute: shader.getAttributeList()) {
        if (names.contains(attribute.getName()) && !bindings.containsKey(attribute)) {
          throw new IllegalArgumentException(String.format("Conflicting types for attribute: %s", attribute.getName()));
        }
        names.add(attribute.getName());
        bindings.put(attribute, location++);
      }
    }
    return bindings;
  }

  protected abstract List<Attribute> getAttributeList();

  protected abstract void getAllUniformLocations();

  protected int getUniformLocation(Uniform uniform) {
    int uniformId = GL20.glGetUniformLocation(programId, uniform.getName());
    uniforms.put(uniform, uniformId);
    return uniformId;
  }

  private void bindAttributes(Map<Attribute, Integer> bindings) {
    for (Attribute attribute: getAttributeList()) {
      if (!bindings.containsKey(attribute)) {
        throw new IllegalArgumentException("No binding found for attribute " + attribute.getName());
      }
      int location = bindings.get(attribute);
      attributes.put(attribute, location);
      GL20.glBindAttribLocation(programId, location, attribute.getName());
    }
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

