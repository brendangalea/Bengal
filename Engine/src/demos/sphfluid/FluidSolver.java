package demos.sphfluid;

import bgmath.Vec3d;
import mintools.parameters.DoubleParameter;
import mintools.swing.VerticalFlowPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 *
 * Created by brendan on 2016-05-06.
 */
public class FluidSolver {

  public DoubleParameter viscosity = new DoubleParameter("Viscosity", 0.1, 0, 100);
  public DoubleParameter gravity = new DoubleParameter("Gravity", 10, 0, 100);
  public DoubleParameter mass = new DoubleParameter("Mass", 0.1, 0.1, 1000);
  public DoubleParameter dt = new DoubleParameter("Time Step", 0.0025, 0.00001, 10);
  public DoubleParameter k = new DoubleParameter("Stiffness", 10, 1e-9, 1e8);
  public DoubleParameter radius = new DoubleParameter("Radius", 2, 0.1, 20);
  public static DoubleParameter ro = new DoubleParameter("Density", 50, 1e-5, 10000);

  public double smoothingLength = 0.6;

  private ParticleSystem ps;
  private Kernel poly6, spiky, visc, cubic;

  public FluidSolver(ParticleSystem ps) {
    this.ps = ps;
    poly6 = new Kernel.Poly6(smoothingLength);
    spiky = new Kernel.Spiky(smoothingLength);
    visc = new Kernel.Viscosity(smoothingLength);
    cubic = new Kernel.CubicSpline2D(smoothingLength);

  }

  public double averageDensity() {
    double density = 0;
    int count = 0;
    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) continue;
      density += particle.computeNextDensity(cubic, ro.getValue());
      count++;
    }
    return density / count;
  }

  public void updateNeighbours() {
    Vec3d tmp = new Vec3d();
    for (ParticleSystem.Particle particle: ps.getParticles()) {
      particle.neighbours.clear();
      particle.boundary.clear();
      for (ParticleSystem.Particle other : ps.getParticles()) {
        if (particle == other) continue;
        double dis = tmp.set(particle.p).sub(other.p).length();
        if (dis < smoothingLength) {
          if (other.isFixed)
            particle.boundary.add(other);
          else
            particle.neighbours.add(other);
        }
      }
    }
  }

  public void pressureSolve() {
    int l = 0;
    while (averageDensity() - ro.getValue() > 1.0 || l < 2) {
      if (l > 50) {
        throw new RuntimeException("Failed to converge");
      }
      for (ParticleSystem.Particle particle: ps.getParticles())
        particle.computeDijPj(cubic, dt.getValue());
      for (ParticleSystem.Particle particle: ps.getParticles())
        particle.computeNextPressure(cubic, ro.getValue(), dt.getValue());
      for (ParticleSystem.Particle particle: ps.getParticles())
        particle.pressure = particle.nextPressure > 0 ? particle.nextPressure : 0;
      for (ParticleSystem.Particle particle: ps.getParticles())
        particle.computePressureDisplacement();
      l++;
    }
  }

  public void predictAdvection() {

    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) continue;
      particle.computeDensity(cubic, ro.getValue());
      // compute advective forces(gravity and viscosity) and update velocity
      particle.f.set(0, -gravity.getValue() * particle.mass, 0);  // initialize with gravity
      particle.f.add(particle.computeViscosity(cubic, viscosity.getValue(), smoothingLength));
    }

    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) continue;
      particle.v.scaleAdd(dt.getValue() / particle.mass, particle.f);
      particle.computeDii(cubic, dt.getValue(), ro.getValue());
    }

    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) continue;
      particle.computeAii(cubic, dt.getValue());
    }

  }

  public void iisphSolve() {
    updateNeighbours();
    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) particle.computeDinv(cubic);
    }
    predictAdvection();
    pressureSolve();

    for (ParticleSystem.Particle particle: ps.getParticles()) {
      if (particle.isFixed) continue;
      Vec3d fp = particle.computeMomentumPreservingPressureForce(dt.getValue());
      particle.v.scaleAdd(0.001 * dt.getValue() / particle.mass, fp);
      particle.p.scaleAdd(dt.getValue(), particle.v);
    }

  }

  public void simpleSolve() {
    updateNeighbours();
    predictAdvection();
    for (ParticleSystem.Particle particle: ps.getParticles())
      particle.computeEosPressure(k.getValue(), ro.getValue());
    for (ParticleSystem.Particle particle: ps.getParticles()) {
      particle.v.scaleAdd(dt.getValue() / particle.mass, particle.computePressureForce(spiky));
      particle.p.scaleAdd(dt.getValue(), particle.v);
    }
    applyBounds();
  }


  public void applyBounds() {
    // bound floor
    for (ParticleSystem.Particle pt: ps.getParticles())
      if (pt.p.y < 0.1 && pt.v.y < 0){
        pt.v.y = 0;
        pt.p.y = 0.1;
      }
    // bound left wall
    for (ParticleSystem.Particle pt: ps.getParticles())
      if (pt.p.x < -5 && pt.v.x < 0){
        pt.v.x = 0;
        pt.p.x = -5;
      }
    // bound right wall
    for (ParticleSystem.Particle pt: ps.getParticles())
      if (pt.p.x > 5 && pt.v.x > 0){
        pt.v.x = 0;
        pt.p.x = 5;
      }
    // sanity check
    float sanity = 40;
    for (ParticleSystem.Particle pt: ps.getParticles()) {
      if (pt.v.length() > sanity){
        pt.v.normalize();
        pt.v.scale(sanity);
      }
    }
  }

  /**
   * Get ParticleSystem parameter controls
   * @return Panel of control sliders
   */
  public JPanel getControls() {
    VerticalFlowPanel vfp = new VerticalFlowPanel();
    vfp.setBorder( new TitledBorder("Particle System Controls" ));
    vfp.add( viscosity.getSliderControls(false));
    vfp.add( gravity.getSliderControls(false));
    vfp.add( mass.getSliderControls(true));
    vfp.add( dt.getSliderControls(true));
    vfp.add( k.getSliderControls(true));
    vfp.add( ro.getSliderControls(true));
    return vfp.getPanel();
  }
}
