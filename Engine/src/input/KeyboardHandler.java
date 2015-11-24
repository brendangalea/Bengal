package input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Handler for keyboard, must be added as observer for window to be use-able
 * Created by Brendan Galea on 11/23/2015.
 */
public class KeyboardHandler extends GLFWKeyCallback {

  public static boolean[] keys = new boolean[65536];

  @Override
  public void invoke(long window, int key, int scancode, int action, int mods) {
    keys[key] = (action != GLFW.GLFW_RELEASE);
    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
      glfwSetWindowShouldClose(window, GLFW_TRUE); // We will detect this in our rendering loop
    }
  }

  public static boolean isKeyDown(int keycode) {
    return keys[keycode];
  }


}
