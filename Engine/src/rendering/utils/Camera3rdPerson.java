package rendering.utils;


import input.XboxControllerHandler;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

/**
 * Camera with functionality to follow a target
 * Created by Brendan Galea on 11/26/2015.
 */
public class Camera3rdPerson extends Camera {

  private float distance = 5f;
  private XboxControllerHandler controller = new XboxControllerHandler(0);
  private static final float ROTATION_SPEED = 2f;
  private static final float MIN_ZOOM = 1f;
  private static final float MAX_ZOOM = 100f;
  private static final float ZOOM_SPEED = 1f;

  public void follow(Vector3f target) {
    float y = distance * (float) Math.sin(Math.toRadians(getPitch())) + target.y;
    float hOffset = distance * (float) Math.cos(Math.toRadians(getPitch()));
    float x = -target.x +  hOffset * (float) Math.sin(Math.toRadians(getYaw()));
    float z = -target.z - hOffset * (float) Math.cos(Math.toRadians(getYaw()));
    setPosition(new Vector3f(-x, y, -z));
  }
  public void update() {

    if (controller.isConnected()) {
      Vector2f stick = controller.getRightJoystick();
      float pitch = getPitch() - stick.y * ROTATION_SPEED;
      float yaw = getYaw() + stick.x * ROTATION_SPEED;
      pitch = Math.min(Math.max(0, pitch), 89);
      yaw %= 360;
      setPitch(pitch);
      setYaw(yaw);
      distance -= ZOOM_SPEED * controller.getTriggers();
      distance = Math.min(Math.max(distance, MIN_ZOOM), MAX_ZOOM);
    }

    updateViewingBuffer();

  }


}
