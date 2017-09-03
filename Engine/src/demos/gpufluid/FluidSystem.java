package demos.gpufluid;

import bgmath.Vec3d;
import rendering.postprocessing.Fbo;
import rendering.postprocessing.ImageRenderer;

import java.awt.*;

/**
 * GPU based particle system (simple eos)
 *
 * Created by brendan on 2016-05-11.
 */
public class FluidSystem {

  ImageRenderer particlePositions; // x, y, condition (+ve for mass, -ve if fixed and condition number)
  ImageRenderer particleVelocities; // vx, vy,
  ImageRenderer densityField;
  ImageRenderer tempField;



  public static void main(String args[]) {

  }


  public static class Particle {
    Vec3d p;
    Vec3d v;
    float mass;


  }

  /* Step the particle system */
  public void step(float dt) {

    // compute laplacian for velocity (hard)
      // plot velocity vx, vy and blur
      // for each sample compute derivatives by fitting polynomial
      // for each sample compute derivatives and sum

    // step state with viscous force and gravity

    // compute new density and pressure

    // update start with pressure force


  }

  public void renderDensity(int texture) {

  }

  public void renderTexture(int texture) {

  }

  public void renderGradient(int texture) {

  }

  public void renderLaplacian(int texture, int channel) {

  }

  public Fbo loadSystemToFbo() {
    return null;
  }


  /* render particle value (i.e Ai) then depending on what is required there is a shader to
   * compute derivative and possibly laplcaian (in case of velocity?)
   */

}
