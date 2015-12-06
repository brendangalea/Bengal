package input;

import org.lwjgl.glfw.GLFW;

import javax.vecmath.Vector2f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Handles button and joystick mapping for an xbox controller
 * Created by Brendan Galea on 11/26/2015.
 */
public class XboxControllerHandler {

  public enum Button {
    A, B, X, Y, LEFT_BUMPER, RIGHT_BUMPER, BACK, START,
    LEFT_STICK, RIGHT_STICK, D_UP, D_DOWN, D_RIGHT, D_LEFT
  }

  private FloatBuffer axes;
  private ByteBuffer buttons;
  private int controllerNumber;
  private static final float DEADZONE = 0.3f;
  private static final float TRIGGER_DEADZONE = 0.01f;


  public XboxControllerHandler(int controllerNumber) {
    this.controllerNumber = controllerNumber;
    if (isConnected()) {
      update();
    }
  }

  public boolean isConnected() {
    return GLFW.glfwGetJoystickName(GLFW.GLFW_JOYSTICK_1 + controllerNumber) != null;
  }

  public void update() {
    axes = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_1 + controllerNumber);
    buttons = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_1 + controllerNumber);
  }

  public Vector2f getLeftJoystick() {
    Vector2f stick =  new Vector2f(axes.get(0), -1 * axes.get(1));
    stick.x = Math.abs(stick.x) < DEADZONE ? 0 :
        Math.signum(stick.x) * (Math.abs(stick.x) - DEADZONE) / (1 - DEADZONE);
    stick.y = Math.abs(stick.y) < DEADZONE ? 0 :
        Math.signum(stick.y) * (Math.abs(stick.y) - DEADZONE) / (1 - DEADZONE);
    return stick;
  }

  public Vector2f getRightJoystick() {
    Vector2f stick =  new Vector2f(axes.get(4), -1 * axes.get(3));
    stick.x = Math.abs(stick.x) < DEADZONE ? 0 :
        Math.signum(stick.x) * (Math.abs(stick.x) - DEADZONE) / (1 - DEADZONE);
    stick.y = Math.abs(stick.y) < DEADZONE ? 0 :
        Math.signum(stick.y) * (Math.abs(stick.y) - DEADZONE) / (1 - DEADZONE);
    return stick;
  }

  public float getTriggers() {
    float trigger = axes.get(2);
    if (Math.abs(trigger) < TRIGGER_DEADZONE)
      return 0;
    return -trigger;
  }

  public boolean isButtonDown(Button button) {
    return buttons.get(button.ordinal()) == 1;
  }

}
