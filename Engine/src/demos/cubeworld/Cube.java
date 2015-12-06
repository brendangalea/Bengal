package demos.cubeworld;

import input.KeyboardHandler;
import input.XboxControllerHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;
import rendering.components.*;
import rendering.utils.Camera;
import rendering.utils.Loader;
import toolbox.utils.Buffers;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.Map;


/**
 * Renderable Cube Model
 * Created by Brendan Galea on 11/25/2015.
 */
public class Cube implements Renderable {

  private RawModel rawModel;
  private int textureId;
  private XboxControllerHandler controller;
  private boolean isGrounded = true;
  private Vector3f center = new Vector3f();

  private Matrix4f transformation;

  private float yvel = 0;
  private static final float g = -9.81f;
  private static final float dt = 0.05f;
  private float speed = 0.1f;

  private int holdCount;
  private boolean stillJumping = false;


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
      20, 21, 22, 21, 22, 23
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
    textureId = new Loader().loadTexture("res/cube_texture.png");
    transformation = new Matrix4f();
    transformation.setIdentity();
    transformation.setTranslation(new Vector3f(0f, 0.5f, 0f));

    controller = new XboxControllerHandler(0);
  }


  public Vector3f getCenter() {
    return center;
  }

  public void update(Camera camera) {

    // physics
    float height = transformation.m13 - 0.5f;
    if (!isGrounded && !stillJumping) {
      yvel += dt * g;
    }
    height += yvel * dt;
    if (height < 0) {
      height = 0;
      isGrounded = true;
    }
    transformation.m13 = height + 0.5f;


    // Control
    boolean isJumpPressed = false;
    if (controller.isConnected()) {
      controller.update();
      Vector2f stick = controller.getLeftJoystick();
      transformation.m23 += (stick.x * speed) *  Math.sin(Math.toRadians(camera.getYaw()))
          -(stick.y * speed) * Math.cos(Math.toRadians(camera.getYaw()));
      transformation.m03 += (stick.x * speed) *  Math.cos(Math.toRadians(camera.getYaw()))
          + (stick.y * speed) * Math.sin(Math.toRadians(camera.getYaw()));
      isJumpPressed = controller.isButtonDown(XboxControllerHandler.Button.A);
    }


    if (isJumpPressed && isGrounded) {
      isGrounded = false;
      yvel = 5;
      stillJumping = true;
      holdCount = 0;
    }

    if (isJumpPressed) {
      if (holdCount++ > 12) {
        stillJumping = false;
      }
    } else {
      stillJumping = false;
    }

    center.set(transformation.m03, transformation.m13, transformation.m23);
  }

  public void release() {
    rawModel.release();
    GL11.glDeleteTextures(textureId);
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

    GL13.glActiveTexture(GL13.GL_TEXTURE0);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

  }

  @Override
  public int getVaoId() {
    return rawModel.getVaoId();
  }


}
