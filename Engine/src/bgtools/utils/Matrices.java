package bgtools.utils;

import rendering.utils.Camera;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * Utility class for setting up various types of projection/viewing matrices
 * Created by Brendan Galea on 11/24/2015.
 */
public class Matrices {

  public static Matrix4f createTransformationMatrix(
      Vector3f translation,
      float rx,
      float ry,
      float rz,
      float scale) {

    Matrix4f matrix = new Matrix4f();
    matrix.setIdentity();
    matrix.rotX((float) Math.toRadians(rx));
    matrix.rotY((float) Math.toRadians(ry));
    matrix.rotZ((float) Math.toRadians(rz));
    matrix.setTranslation(translation);
    matrix.mul(scale);
    return matrix;
  }

  public static Matrix4f createProjectionMatrix(float near, float far, float fov, int width, int height) {
    float aspectRatio = (float) width / (float) height;
    float yScale = (float) (1f / Math.tan(Math.toRadians(fov / 2f)) * aspectRatio);
    float xScale = yScale / aspectRatio;
    float frustumLength = far - near;

    Matrix4f matrix = new Matrix4f();
    matrix.m00 = xScale;
    matrix.m11 = yScale;
    matrix.m22 = -((far + near) / frustumLength);
    matrix.m32 = -1;
    matrix.m23 = -((2 * near * far) / frustumLength);
    matrix.m33 = 0;
    return matrix;
  }

  public static Matrix4f createViewMatrix(Camera camera) {
    Matrix4f matrix = new Matrix4f();
    matrix.setIdentity();
    matrix.rotX((float) Math.toRadians(camera.getPitch()));
    matrix.rotY((float) Math.toRadians(camera.getYaw()));
    Vector3f translation = new Vector3f();
    translation.negate(camera.getPosition());
    matrix.setTranslation(translation);
    return matrix;
  }

}
