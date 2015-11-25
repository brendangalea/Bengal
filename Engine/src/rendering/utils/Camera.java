package rendering.utils;

import input.KeyboardHandler;
import org.lwjgl.glfw.GLFW;

import javax.vecmath.Vector3f;

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


  public void move() {
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
      pitch -= delta*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
      pitch += delta*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
      yaw += delta*60;
    }
    if (KeyboardHandler.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
      yaw -= delta*60;
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

  }

  public Vector3f getPosition() {
    return position;
  }

  public void setPosition(Vector3f position) {
    this.position = position;
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


}