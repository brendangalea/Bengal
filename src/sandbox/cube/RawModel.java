package sandbox.cube;

/**
 * Most basic model for storing a vertex array object
 * Created by Brendan Galea on 11/22/2015.
 */
public class RawModel {

  private int vaoId;
  private int vertexCount;

  public RawModel(int vaoId, int vertexCount) {
    this.vaoId = vaoId;
    this.vertexCount = vertexCount;
  }

  public int getVaoId() {
    return vaoId;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
