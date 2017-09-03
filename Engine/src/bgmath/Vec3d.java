package bgmath;

/**
 * Mutable vector class, all operations done in place and return
 * the calling object itself
 * Created by brendan on 16-03-18.
 */
public class Vec3d {

  public double x, y, z;

  public Vec3d() {}

  public Vec3d(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vec3d(Vec3d v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
  }

  public Vec3d copy() {
    return new Vec3d(this);
  }

  public Vec3d set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    return this;
  }

  public Vec3d set(Vec3d v) {
    this.x = v.x;
    this.y = v.y;
    this.z = v.z;
    return this;
  }

  public Vec3d add(Vec3d v) {
    this.x += v.x;
    this.y += v.y;
    this.z += v.z;
    return this;
  }

  public Vec3d sub(Vec3d v) {
    this.x -= v.x;
    this.y -= v.y;
    this.z -= v.z;
    return this;
  }

  public Vec3d scale(double s) {
    this.x *= s;
    this.y *= s;
    this.z *= s;
    return this;
  }

  public Vec3d scaleAdd(double s, Vec3d v) {
    this.x += s * v.x;
    this.y += s * v.y;
    this.z += s * v.z;
    return this;
  }

  public double lengthSquared() {
    return x * x + y * y + z * z;
  }

  public double length() {
    return Math.sqrt(x * x + y * y + z * z);
  }

  public Vec3d normalize() {
    double length = Math.sqrt(x * x + y * y + z * z);
    if (length == 0) {
      throw new RuntimeException("Cannot normalize 0 length vector");
    }
    double invLength = 1.0 / length;
    this.x *= invLength;
    this.y *= invLength;
    this.z *= invLength;
    return this;
  }

  public double dot(Vec3d v) {
    return x * v.x + y * v.y + z * v.z;
  }

  public Vec3d cross(Vec3d v) {
    double x = this.y * v.z - this.z * v.y;
    double y = this.z * v.x - this.x * v.z;
    this.z = this.x * v.y - this.y * v.z;
    this.x = x;
    this.y = y;
    return this;
  }

  public Vec3d project(Vec3d v) {
    double s = (x * v.x + y * v.y + z * v.z) / v.lengthSquared();
    this.x = v.x * s;
    this.y = v.y * s;
    this.z = v.z * s;
    return this;
  }

  public String toString() {
    return String.format("(%f, %f, %f)", x, y, z);
  }
}
