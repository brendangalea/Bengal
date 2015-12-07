package sandbox;

import javax.vecmath.Vector3f;

/**
 *
 * Created by brendan on 15-12-07.
 */
public class VectorTesting {

  public static void main(String[] args) {
    Vector3f v = new Vector3f(1, 2, 3);
    Vector3f u = new Vector3f(1, 1, 1);

    v.cross(v, u);
    System.out.println("v = " + v);
  }
}
