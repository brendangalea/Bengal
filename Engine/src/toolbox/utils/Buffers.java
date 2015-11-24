package toolbox.utils;

import org.lwjgl.BufferUtils;

import javax.vecmath.Matrix4f;
import java.nio.FloatBuffer;

/**
 * Tool set for various buffer functions
 *
 * Created by brendan on 15-07-12.
 */
public class Buffers {

  public static FloatBuffer identityMatrix4f() {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    buffer.put(1.f).put(0.f).put(0.f).put(0.f);
    buffer.put(0.f).put(1.f).put(0.f).put(0.f);
    buffer.put(0.f).put(0.f).put(1.f).put(0.f);
    buffer.put(0.f).put(0.f).put(0.f).put(1.f);
    buffer.flip();
    return buffer;
  }

  public static FloatBuffer bufferWithMatrix(Matrix4f matrix) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    buffer.put(matrix.m00).put(matrix.m01).put(matrix.m02).put(matrix.m03);
    buffer.put(matrix.m10).put(matrix.m11).put(matrix.m12).put(matrix.m13);
    buffer.put(matrix.m20).put(matrix.m21).put(matrix.m22).put(matrix.m23);
    buffer.put(matrix.m30).put(matrix.m31).put(matrix.m32).put(matrix.m33);
    buffer.flip();
    return buffer;
  }

  public static FloatBuffer bufferWithValue(float value, int repeat) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(repeat);
    for (int i = 0; i < repeat; i++) {
      buffer.put(value);
    }
    buffer.flip();
    return buffer;
  }

  public static FloatBuffer bufferWithPattern(float[] pattern, int repeat) {
    FloatBuffer buffer = BufferUtils.createFloatBuffer(pattern.length * repeat);
    for (int i = 0; i < repeat; i++) {
      buffer.put(pattern);
    }
    buffer.flip();
    return buffer;
  }


}