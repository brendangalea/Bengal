package rendering.utils;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import demos.cubeworld.Cube;
import demos.cubeworld.Skybox;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import rendering.components.Attribute;
import rendering.components.Raster;
import rendering.components.Renderable;
import rendering.components.ShaderProgram;
import rendering.shaders.diffuse.DiffuseShader;
import rendering.shaders.skybox.SkyboxShader;

import javax.vecmath.Vector3f;

import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

/**
 * Class for viewing 3D objects with camera controls to
 * zoom, pan and rotate the object
 * Created by brendan on 15-12-07.
 */
public class Viewer {

  private WindowManager windowManager = new WindowManager("Viewing Window", 960, 640);
  private Renderable renderableObject;

  public static void main(String args[]) {
    new Viewer(null).run();
  }

  public Viewer(Renderable renderableObject) {
    this.renderableObject = renderableObject;
  }

  public void run() {
    try {
      windowManager.init();

      // game loop
      GL.createCapabilities();

      Raster raster = new Raster(windowManager);
      Camera camera = new Camera();
      camera.setPosition(0, 0.5f, -4);
      camera.setYaw(180);
      DiffuseShader diffuseShader = new DiffuseShader();
      diffuseShader.init();
      Cube cube = new Cube(diffuseShader.getAttributes());
      raster.addObject(cube);

      SkyboxShader skyboxShader = new SkyboxShader();
      Raster skyboxRaster = new Raster(windowManager);
      skyboxShader.init();
      Skybox skybox = new Skybox(skyboxShader.getAttributes());
      skyboxRaster.addObject(skybox);
      glClearColor(0x5d / 255.f, 0xbf / 255.f, 0xde / 255.f, 0.0f);


      // set up shaders and load any objects
      while (!windowManager.shouldClose()) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        // update
        camera.update();

        // render
        diffuseShader.start();
        raster.render(diffuseShader, camera);
        diffuseShader.stop();
        skyboxShader.start();
        skyboxRaster.render(skyboxShader, camera);
        skyboxShader.stop();

        glfwSwapBuffers(windowManager.getWindowId()); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
      }

      // clean up shaders and any loaded objects
      diffuseShader.cleanUp();

      windowManager.release();
    } finally {
      windowManager.terminate();
    }
  }

}
