package demos.cubeworld;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.shaders.sandbox.SandboxShader;
import rendering.utils.Camera;
import rendering.components.RawModel;
import rendering.components.ShaderProgram;
import rendering.utils.Loader;
import rendering.utils.WindowManager;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

/**
 * Main Class for Cube World Demo
 * Created by Brendan Galea on 11/23/2015.
 */
public class CubeWorld {

  private static final float[] vertices = {
      -0.5f, 0.5f, 0f,
      -0.5f, -0.5f, 0f,
      0.5f, -0.5f, 0f,
      0.5f, 0.5f, 0f,
  };

  private static final int[] indices = {
      0, 1, 3, 3, 1, 2
  };

  private static final float[] uvs = {
      0.f, 0.f,
      0.f, 1.f,
      1.f, 1.f,
      1.f, 0.f
  };

  private WindowManager windowManager = new WindowManager("Cube Demo", 480, 320);
  private Camera camera = new Camera();
  private ShaderProgram shader;

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

  private void setupViewingMatrices() {
    // set up projection
//    FloatBuffer projectionBuffer = Buffers.bufferWithMatrix(
//        Matrices.createProjectionMatrix(0.1f, 1000f, 70f, windowManager.getWidth(), windowManager.getHeight()));
//    GL20.glUniformMatrix4fv(
//        shader.getUniforms().get(Uniform.PROJECTION),
//        false,
//        projectionBuffer);
//    // Transformation
//    FloatBuffer transformation = Buffers.identityMatrix4f();
//    GL20.glUniformMatrix4fv(
//        shader.getUniforms().get(Uniform.TRANSFORMATION),
//        false,
//        transformation);
//    // Inverse Transform
//    GL20.glUniformMatrix4fv(
//        shader.getUniforms().get(Uniform.INVERSE_TRANSFORMATION),
//        false,
//        transformation);
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
    Loader loader = new Loader();
    RawModel model = loader.loadToVao(vertices, uvs, indices);
    shader = new SandboxShader();
    shader.init();
    shader.start();

    // Set the clear color
    glClearColor(0x5d / 255.f, 0xbf / 255.f, 0xde / 255.f, 0.0f);

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!windowManager.shouldClose()) {
      camera.move();
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
      render(model);

      glfwSwapBuffers(windowManager.getWindowId()); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }

    shader.cleanUp();
    loader.cleanUp();
  }
}
