package demos.sphfluid;

import demos.cubeworld.Skybox;
import demos.cubeworld.Terrain;
import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.VerticalFlowPanel;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import rendering.components.Raster;
import rendering.components.ShaderProgram;
import rendering.shaders.diffuse.DiffuseShader;
import rendering.shaders.skybox.SkyboxShader;
import rendering.shaders.sphere.SphereShader;
import rendering.utils.Camera;
import rendering.utils.WindowManager;


import javax.swing.border.TitledBorder;
import javax.vecmath.Vector3f;

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

/**
 * Demo of a 2D fluid simulation
 * Created by brendan on 2016-05-05.
 */
public class FluidDemo {

  private WindowManager windowManager = new WindowManager("Fluid Demo", 960, 640);
  private ParticleSystem ps = new ParticleSystem();
  private FluidSolver solver = new FluidSolver(ps);

  public static void main(String[] args) {
    new FluidDemo().run();
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

  public void loop() {
    // This line is critical for LWJGL's interoperation with GLFW's
    // OpenGL context, or any context that is managed externally.
    // LWJGL detects the context that is current in the current thread,
    // creates the GLCapabilities instance and makes the OpenGL
    // bindings available for use.
    GL.createCapabilities();

    ShaderProgram shader = new DiffuseShader();
    shader.init();

    Camera camera = new Camera();
    camera.setPosition(new Vector3f(0, 1, -10.0f));
    camera.setPitch(0);
    camera.setYaw(180);
    Terrain terrain = new Terrain(new Vector3f(-400, 0, -400), shader.getAttributes());
    Raster raster = new Raster(windowManager);
    raster.addObject(terrain);


    Raster fluidRaster = new Raster(windowManager);
    ShaderProgram sphereShader = new SphereShader();
    sphereShader.init();
    ps.setupRendering(sphereShader);
    fluidRaster.addObject(ps);

    SkyboxShader skyboxShader = new SkyboxShader();
    Raster skyboxRaster = new Raster(windowManager);
    skyboxShader.init();
    Skybox skybox = new Skybox(skyboxShader.getAttributes());
    skyboxRaster.addObject(skybox);


    // Set the clear color
    glClearColor(0x5d / 255.f, 0xbf / 255.f, 0xde / 255.f, 0.0f);

    // Run the rendering loop until the user has attempted to close
    // the window or has pressed the ESCAPE key.
    while (!windowManager.shouldClose()) {

      camera.update();
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

      raster.prepare();
      shader.start();
      raster.render(shader, camera);
      shader.stop();

      skyboxShader.start();
      skyboxRaster.render(skyboxShader, camera);
      skyboxShader.stop();

      solver.simpleSolve();
      ps.updateRendering();
      glEnable(GL_BLEND);
      glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
      sphereShader.start();
      fluidRaster.render(sphereShader, camera);
      sphereShader.stop();
      glDisable(GL_BLEND);


      glfwSwapBuffers(windowManager.getWindowId()); // swap the color buffers

      // Poll for window events. The key callback above will only be
      // invoked during this call.
      glfwPollEvents();
    }

    shader.cleanUp();
    sphereShader.cleanUp();
    skyboxShader.cleanUp();
    terrain.release();
    skybox.release();
  }

  /* App Parameters */
  private BooleanParameter run = new BooleanParameter( "animate", false );
  private BooleanParameter useFasterSolver = new BooleanParameter( "Use Faster Solver", true);
  private DoubleParameter scale = new DoubleParameter( "Scale", 1, 0.01, 1);
  private DoubleParameter alphaScale = new DoubleParameter( "alpha scale", 0.5, 0, 1);
  private DoubleParameter colorScale = new DoubleParameter( "color scale", 1, 1e-1, 1e1 );
  private BooleanParameter drawInfo = new BooleanParameter( "draw info", true );
  private IntParameter substeps = new IntParameter( "Substeps", 10, 1, 40);
  private IntParameter dropHeight = new IntParameter( "Drop Height", 0, 0, 200);
  private IntParameter numPts = new IntParameter( "# of Particles", 400, 0, 8000);
  private IntParameter leftInset = new IntParameter( "Left Inset", 0, 0, 300);
  private IntParameter rightInset = new IntParameter( "Right Inset", 0, 0, 300);
  private IntParameter drawRadius = new IntParameter( "Draw Radius", 2, 1, 16);
  private BooleanParameter record = new BooleanParameter( "record each step to image file (press ENTER in canvas to toggle)", false );

  public JPanel getControls() {
    VerticalFlowPanel vfp = new VerticalFlowPanel();
    vfp.add( run.getControls() );
    vfp.add( record.getControls() );
    vfp.add( numPts.getSliderControls());
    vfp.add( colorScale.getSliderControls(true));
    vfp.add( alphaScale.getSliderControls(false));
    vfp.add( scale.getSliderControls(false));
    vfp.add( dropHeight.getSliderControls());
    vfp.add( substeps.getSliderControls());
    vfp.add( leftInset.getSliderControls());
    vfp.add( rightInset.getSliderControls());
    vfp.add( drawRadius.getSliderControls());
    vfp.add( useFasterSolver.getControls());
    vfp.add( solver.getControls());

    VerticalFlowPanel vfp2 = new VerticalFlowPanel();
    vfp2.setBorder( new TitledBorder("display controls" ));
    vfp2.add( drawInfo.getControls() );

    vfp.add( vfp2.getPanel() );

    return vfp.getPanel();
  }

}
