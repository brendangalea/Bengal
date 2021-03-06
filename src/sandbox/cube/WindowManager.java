package sandbox.cube;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {

  // We need to strongly reference callback instances.
  private GLFWErrorCallback errorCallback;
  private GLFWKeyCallback keyCallback;
  private int width, height;
  private String title;
  // The window handle
  private long window;

  public WindowManager(String title, int width, int height) {
    this.title = title;
    this.width = width;
    this.height = height;
  }

  public void init() {
    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if ( glfwInit() != GLFW_TRUE )
      throw new IllegalStateException("Unable to initialize GLFW");

    // Configure our window
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

    int WIDTH = width;
    int HEIGHT = height;

    // Create the window
    window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
    if ( window == NULL )
      throw new RuntimeException("Failed to create the GLFW window");

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
      @Override
      public void invoke(long window, int key, int scancode, int action, int mods) {
        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
          glfwSetWindowShouldClose(window, GLFW_TRUE); // We will detect this in our rendering loop
      }
    });

    // Get the resolution of the primary monitor
    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    // Center our window
    glfwSetWindowPos(
        window,
        (vidmode.width() - WIDTH) / 2,
        (vidmode.height() - HEIGHT) / 2
    );

    // Make the OpenGL context current (links gl current context to this window)
    glfwMakeContextCurrent(window);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(window);
  }


  public void release() {
    // Release window and window callbacks
    glfwDestroyWindow(window);
    keyCallback.release();
  }

  public void terminate() {
    // Terminate GLFW and release the GLFWErrorCallback
    glfwTerminate();
    errorCallback.release();
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(window) == GLFW_TRUE;
  }

  public long getWindowId() {
    return window;
  }

}
