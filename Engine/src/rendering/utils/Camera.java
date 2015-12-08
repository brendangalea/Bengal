package rendering.utils;

import input.KeyboardHandler;
import org.lwjgl.glfw.GLFW;
import bgtools.utils.Buffers;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;

/**
 *
 * Created by Brendan Galea on 11/23/2015.
 */
public class Camera {

  private Vector3f position = new Vector3f(0, 0, 0);
  private float pitch;
  private float roll;
  private float yaw;
  private float speed = 0.01f;
  private Matrix4f viewingMatrix = new Matrix4f();
  private FloatBuffer viewingBuffer = Buffers.identityMatrix4f();

  public Matrix4f getViewingMatrix() {
    return viewingMatrix;
  }

  public FloatBuffer getViewingBuffer() {
    return viewingBuffer;
  }

  public void update() {

    float delta = speed;
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
      delta = speed * 5;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_F)) {
      speed += 0.01f;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_C)) {
      speed = Math.max(0.01f, speed - 0.01f);
    }

    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_E)) {
      position.y += delta;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_Q)) {
      position.y -= delta;
    }

    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_UP)) {
      pitch -= 0.03*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
      pitch += 0.03*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
      yaw += 0.03*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
      yaw -= 0.03*60;
    }


    // Do motion in flat plane oriented to view direction
    Vector3f lateralMotion = new Vector3f(0, 0, 0);
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_W)) {
      lateralMotion.z -= delta;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_S)) {
      lateralMotion.z += delta;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_A)) {
      lateralMotion.x -= delta;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_D)) {
      lateralMotion.x += delta;
    }
    double radians = Math.toRadians(yaw);
    position.x += -lateralMotion.z*Math.sin(radians) + lateralMotion.x*Math.cos(radians);
    position.z += lateralMotion.x*Math.sin(radians) + lateralMotion.z*Math.cos(radians);

    updateViewingBuffer();
  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(Vector3f position) {
    this.position.set(position);
  }

  public void setPosition(float x, float y, float z) {
    this.position.set(x, y, z);
  }

  public float getPitch() {
    return pitch;
  }

  public void setPitch(float pitch) {
    this.pitch = pitch;
  }

  public float getRoll() {
    return roll;
  }

  public void setRoll(float roll) {
    this.roll = roll;
  }

  public float getYaw() {
    return yaw;
  }

  public void setYaw(float yaw) {
    this.yaw = yaw;
  }

  public void lookAt(Vector3f target, Vector3f up) {

    Vector3f zAxis = new Vector3f();
    Vector3f yAxis = new Vector3f();
    Vector3f xAxis = new Vector3f();

    zAxis.sub(target, position);
    zAxis.normalize();

    xAxis.cross(up, zAxis);
    xAxis.normalize();

    yAxis.cross(zAxis, xAxis);

    viewingMatrix.setColumn(0, xAxis.x, xAxis.y, xAxis.z, -xAxis.dot(position));
    viewingMatrix.setColumn(1, yAxis.x, yAxis.y, yAxis.z, -yAxis.dot(position));
    viewingMatrix.setColumn(2, zAxis.x, zAxis.y, zAxis.z, -zAxis.dot(position));
    viewingMatrix.setColumn(3, 0, 0, 0, 1);

    updateViewingBuffer();
  }

  protected void updateViewingBuffer() {
    // TODO optimize this viewing transform
    viewingMatrix.setIdentity();

    viewingMatrix.setColumn(3, -position.x, -position.y, -position.z, 1.0f);
    Matrix4f m1 = new Matrix4f();
    Matrix4f m2 = new Matrix4f();

    m1.rotX((float) Math.toRadians(pitch));
    m2.rotY((float) Math.toRadians(yaw));
    viewingMatrix.mul(m2, viewingMatrix);
    viewingMatrix.mul(m1, viewingMatrix);

    viewingBuffer.clear();
    Buffers.put(viewingMatrix, viewingBuffer);
    viewingBuffer.flip();
  }

}