package sandbox.cube;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * Created by Brendan Galea on 11/22/2015.
 */
public class Cube {

  private static final float[] vertices = {
      -0.5f, 0.5f, 0f,
      -0.5f, -0.5f, 0f,
      0.5f, -0.5f, 0f,
      0.5f, -0.5f, 0f,
      0.5f, 0.5f, 0f,
      -0.5f, 0.5f, 0f
  };

  private WindowManager windowManager = new WindowManager("Cube Demo", 480, 320);

  public static void main(String[] args) {
    new Cube().run();
  }

  public void run() {

    System.out.println("Hello LWJGL " + Version.getVersion() + "!");
    try {
      windowManager.init();
      loop();
      windowManager.release();
    } finally {
      windowManager.terminate();
    }
  }

  public void render(RawModel model) {
    GL30.glBindVertexArray(model.getVaoId());
    GL20.glEnableVertexAttribArray(0);
    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    GL20.glDisableVertexAttribArray(0);
    GL30.glBindVertexArray(0);
  }
  public void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // Set the clear color
    glClearColor(0x5d/255.f, 0xbf/255.f, 0xde/255.f, 0.0f);

    Loader loader = new Loader();

    RawModel model = loader.loadToVao(vertices);
    System.out.println(model.getVertexCount());
    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!windowManager.shouldClose()) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

      render(model);

      glfwSwapBuffers(windowManager.getWindowId()); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }

    loader.cleanUp();
  }
}
