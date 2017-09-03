package demos.sphfluid;

import bgmath.Vec3d;
import bgtools.utils.Buffers;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import rendering.components.Attribute;
import rendering.components.Renderable;
import rendering.components.ShaderProgram;
import rendering.components.Uniform;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map;

import static rendering.components.Attribute.POSITION;
import static rendering.components.Attribute.RADIUS;
import static rendering.components.Attribute.COLOR;


/**
 * Class for rendering a particle system as spheres
 * Created by brendan on 2016-05-05.
 */
public class ParticleSystem implements Renderable {

  // TODO glMapBuffer for data savings?

  private int vaoId;

  // px, py, pz, r, cx, cy, cz
  private int vboId;


  private ArrayList<Particle> particles = new ArrayList<>();
  private Matrix4f transformation;
  private float radius = 0.1f;
  int row = 5;
  int col = 20;

  public ParticleSystem() {

    transformation = new Matrix4f();
    transformation.setIdentity();
    for (int i = 0; i < col; i++) {
      for (int j = 0; j < row; j++) {
        double offset =  j % 2 == 0 ? radius : 0;
        double x = 2 - 2 * i * radius * 1.1 + offset;
        double y = 2.5 + 2 * j * radius * 1;
        particles.add(new Particle(x, y, 0));
      }
    }
//    particles.add(new Particle(0.0, 3.0, 0));
    for(int i = 0; i < 2 * col; i++) {
      double x = 4.5 - 2 * i * radius * 1.1;
      double y = radius;
      Particle p = new Particle(x, y, 0);
      p.isFixed = true;
      particles.add(p);
    }
    for(int i = 0; i < 2 * col; i++) {
      double x = 4.6 - 2 * i * radius * 1.1;
      double y = radius;
      Particle p = new Particle(x, y, 0);
      p.isFixed = true;
      particles.add(p);
    }

  }

//  public void addParticlesFilledRegion(Vec3d a, Vec3d b, double height, Particle template, double h) {
//    double spac
//  }

  public ArrayList<Particle> getParticles() {
    return particles;
  }


  public void setupRendering(ShaderProgram program) {

    int positionId = program.getAttributes().get(Attribute.POSITION);
    int colorId = program.getAttributes().get(Attribute.COLOR);
    int radiusId = program.getAttributes().get(Attribute.RADIUS);

    // create shader
    vaoId = GL30.glGenVertexArrays();
    GL30.glBindVertexArray(vaoId);

    FloatBuffer interleavedBuffer = interleavedParticleBuffer();

    vboId = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, interleavedBuffer, GL15.GL_STREAM_DRAW);

    GL20.glVertexAttribPointer(positionId, POSITION.getSize(), GL11.GL_FLOAT, false, 28, 0);
    GL20.glVertexAttribPointer(radiusId, RADIUS.getSize(), GL11.GL_FLOAT, false, 28, 12);
    GL20.glVertexAttribPointer(colorId, COLOR.getSize(), GL11.GL_FLOAT, false, 28, 16);

    GL33.glVertexAttribDivisor(0, 1);
    GL33.glVertexAttribDivisor(1, 1);
    GL33.glVertexAttribDivisor(2, 1);


    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL30.glBindVertexArray(0);
  }

  /**
   * Updates particle system to be ready to render
   */
  public void updateRendering() {
    FloatBuffer interleavedBuffer = interleavedParticleBuffer();
    GL30.glBindVertexArray(vaoId);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, interleavedBuffer);
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    GL30.glBindVertexArray(0);
  }

  public void release() {
    GL30.glDeleteVertexArrays(vaoId);
    GL15.glDeleteBuffers(vboId);
  }

  @Override
  public int getVaoId() {
    return vaoId;
  }

  @Override
  public void draw(ShaderProgram shader) {


    Map<Uniform, Integer> uniforms = shader.getUniforms();

    if (uniforms.containsKey(Uniform.TRANSFORMATION)) {
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.TRANSFORMATION),
          false,
          Buffers.bufferWithMatrix(transformation));
    }

    if (uniforms.containsKey(Uniform.INVERSE_TRANSFORMATION)) {
      // TODO invert transformation matrix
      GL20.glUniformMatrix4fv(
          shader.getUniforms().get(Uniform.INVERSE_TRANSFORMATION),
          false,
          Buffers.bufferWithMatrix(transformation));
    }

    GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, particles.size());

  }

  private FloatBuffer interleavedParticleBuffer() {
    FloatBuffer interleavedBuffer = BufferUtils.createFloatBuffer(7 * particles.size());
    for (Particle particle: particles) {
      Vec3d c = new Vec3d(0.3, 0.3, 0.3);
      double density = particle.density / FluidSolver.ro.getValue();
      density = density > 0 ? (density < 1 ? density : 1) : 0;
      if (!particle.isFixed) {
        c.set(density, particle.pressure, 0);
      }
      interleavedBuffer
          .put((float) particle.p.x)
          .put((float) particle.p.y)
          .put((float) particle.p.z)
          .put(radius)
          .put((float) c.x)
          .put((float) c.y)
          .put((float) c.z);
    }
    interleavedBuffer.flip();
    return interleavedBuffer;
  }

  public static class Particle {
    Vec3d p = new Vec3d();
    Vec3d v = new Vec3d();
    Vec3d f = new Vec3d();
    Vec3d dii = new Vec3d();
    Vec3d dijpj = new Vec3d();
    Vec3d pd = new Vec3d();
    double dinv = 0;
    double aii = 0;
    double mass = 4.19;
    double density = 0;
    double volume = 0;
    double pressure = 0;
    double nextPressure = 0;
    boolean isFixed = false;
    ArrayList<Particle> neighbours = new ArrayList<>();
    ArrayList<Particle> boundary = new ArrayList<>();


    public Particle() {}

    public Particle(double x, double y, double z) {
      p.set(x, y, z);
    }

    public double distance(Particle other) {
      Vec3d tmp = new Vec3d(p);
      return tmp.sub(other.p).length();
    }

    public void computeDinv(Kernel k) {
      if (!isFixed) return; // only boundary particles have delta inverse

      dinv = k.weight(0);
      for (Particle other: boundary) {
          dinv += k.weight(distance(other));
      }
      dinv = 1.0 / dinv;
    }

    public void computeDensity(Kernel k, double ro) {
      if (isFixed) return;
      density = k.weight(0) * mass;
      for (Particle other: neighbours) {
        density += other.mass * computeWeight(k, other);
      }
      for (Particle other: boundary) {
        density += ro * other.dinv * computeWeight(k, other);
      }

      volume = mass / density;
    }

    public double computeWeight(Kernel k, Particle other) {
      return k.weight(distance(other));
    }

    public Vec3d computeGradient(Kernel k, Particle other) {
      Vec3d result = new Vec3d(p).sub(other.p).normalize();
      result.scale(k.gradient(distance(other)));

      return result;
    }

    public double computeLaplacian(Kernel k, Particle other) {
      double dis = distance(other);
      return k.laplacian(dis);
    }

    public Vec3d computeViscosity(Kernel k, double visc, double h) {
      Vec3d force = new Vec3d();
      Vec3d tmp = new Vec3d();
      double ro = FluidSolver.ro.getValue();
      for (Particle other: neighbours) {
        tmp.set(p).sub(other.p);
        double value = -visc * Math.min(v.copy().sub(other.v).dot(tmp), 0) / (tmp.lengthSquared() + 0.01 * h * h);
        force.scaleAdd(-value * mass * other.mass, computeGradient(k, other));
      }

      for (Particle other: boundary) {
        tmp.set(p).sub(other.p);
        double value = -visc * Math.min(v.copy().sub(other.v).dot(tmp), 0) / (tmp.lengthSquared() + 0.01 * h * h);
        force.scaleAdd(-value * mass * other.dinv * ro, computeGradient(k, other));
      }

      return force;
    }

    public Vec3d computePressureForce(Kernel k) {
      Vec3d force = new Vec3d();
      for (Particle other: neighbours) {
        Vec3d gradient = computeGradient(k, other);
        gradient.scale((pressure + other.pressure) / (2 * other.density));
        force.add(gradient);
      }
      return force.scale(-mass);
    }

    public Vec3d computeMomentumPreservingPressureForce(double dt) {
      Vec3d force = new Vec3d(pd);
      if (isFixed) return force;
      return force.scale(mass / (dt * dt));
    }

    public void computeEosPressure(double stiffness, double restDensity) {
      pressure = stiffness * (density - restDensity);
      pressure = pressure < 0 ? 0 : pressure;
    }

    public void computeDii(Kernel k, double dt, double ro) {
      if (isFixed) return;
      dii.set(0, 0, 0);
      double d2inv = 1.0 / (density * density);
      for (Particle other: neighbours) {
        dii.scaleAdd(other.mass * d2inv, computeGradient(k, other));
      }

      for (Particle other: boundary) {
        dii.scaleAdd(other.dinv * ro * d2inv, computeGradient(k, other));
      }
      dii.scale(-dt * dt);
    }

    public Vec3d computeDij(Kernel k, Particle other, double dt) {
      double scale = (-dt * dt * other.mass) / (other.density * other.density);
      return computeGradient(k, other).scale(scale);
    }

    public void computeDijPj(Kernel k, double dt) {
      if (isFixed) return;
      dijpj.set(0, 0, 0);
      for (Particle other: neighbours) {
        if (other.isFixed) System.out.println("neighbour should not be fixed 1");
        double scale = (other.mass * other.pressure) / (other.density * other.density);
        dijpj.add(computeGradient(k, other).scale(scale));
      }
      dijpj.scale(-dt * dt);
    }

    public void computeAii(Kernel k, double dt) {
      if (isFixed) return;
      double ro = FluidSolver.ro.getValue();
      double update = 0;
      for (Particle other: neighbours) {
        update += v.copy().sub(other.v).scale(other.mass).dot(computeGradient(k, other));
      }
      for (Particle other: boundary) {
        update += other.dinv * FluidSolver.ro.getValue() * v.copy().sub(other.v).dot(computeGradient(k, other));
      }
      density += update * dt;
      pressure *= 0.5;

      aii = 0;
      for (Particle other: neighbours) {
        aii += other.mass * dii.copy().sub(other.computeDij(k, this, dt)).dot(computeGradient(k, other));
      }
      for (Particle other: boundary) {
        aii += other.dinv * ro * dii.dot(computeGradient(k, other));
      }

    }

    public Vec3d computePressureDisplacement() {
      pd.set(dijpj);
      pd.scaleAdd(pressure, dii);
      return pd;
    }

    public double computeNextDensity(Kernel k, double ro) {
      double next = density;
      for (Particle other: neighbours) {
        next += other.mass * pd.copy().sub(other.pd).dot(computeGradient(k, other));
      }
      for (Particle other: boundary) {
        next += other.dinv * ro * pd.dot(computeGradient(k, other));
      }
      return next;
    }


    public void computeNextPressure(Kernel k, double ro, double dt) {
      if (isFixed) return;
      if (aii == 0) return;
      double value = 0;
      Vec3d tmp = new Vec3d();
      for (Particle other: neighbours) {
        if (other.isFixed) System.out.println("neighbour should not be fixed 3");

        Vec3d gradient = computeGradient(k, other);
        tmp.set(dijpj);
        tmp.scaleAdd(-other.pressure, other.dii); // -djjpj
        tmp.scaleAdd(-1, other.dijpj); // -djkpk
        tmp.scaleAdd(pressure, other.computeDij(k, this, dt)); //djipi
        value += other.mass * tmp.dot(gradient);
      }
      double value2 = 0;
      for (Particle other: boundary) {
        value2 += computeGradient(k, other).dot(dijpj) * other.dinv* ro;
      }
      nextPressure = 0.5 * pressure + (0.5 / aii) * (ro - density - value - value2);
    }
  }
}
