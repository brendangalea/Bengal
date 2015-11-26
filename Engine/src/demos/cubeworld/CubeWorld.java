package demos.cubeworld;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.components.Raster;
import rendering.shaders.diffuse.DiffuseShader;
import rendering.shaders.phong.PhongShader;
import rendering.shaders.sandbox.SandboxShader;
import rendering.utils.Camera;
import rendering.components.RawModel;
import rendering.components.ShaderProgram;
import rendering.utils.Loader;
import rendering.utils.WindowManager;

import javax.vecmath.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

/**
 * Main Class for Cube World Demo
 * Created by Brendan Galea on 11/23/2015.
 */
public class CubeWorld {

  private WindowManager windowManager = new WindowManager("Cube Demo", 480, 320);
  private Camera camera = new Camera();

  public static void main(String[] args) {
    new CubeWorld().run();
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
//    FloatBuffer viewBuffer = Buffers.bufferWithMatrix(Matrices.createViewMatrix(camera));
//    GL20.glUniformMatrix4fv(
//        shader.getUniforms().get(Uniform.VIEWING),
//        false,
//        viewBuffer);

    GL30.glBindVertexArray(model.getVaoId());
    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
//    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
    GL20.glDisableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);
    GL30.glBindVertexArray(0);
  }


  public void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    // initialize phong shader
//    shader = new PhongShader();
    ShaderProgram shader = new DiffuseShader();
    shader.init();
    shader.start();

    camera.setPosition(new Vector3f(0, 0, -5.0f));
    camera.setYaw(180);
    Cube cube = new Cube(shader.getAttributes());
    Raster raster = new Raster(windowManager);
    raster.addObject(cube);

    // Set the clear color
    glClearColor(0x5d / 255.f, 0xbf / 255.f, 0xde / 255.f, 0.0f);

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!windowManager.shouldClose()) {
      camera.move();
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
      raster.prepare();
      raster.render(shader, camera);
      glfwSwapBuffers(windowManager.getWindowId()); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }

    shader.cleanUp();
  }
}
