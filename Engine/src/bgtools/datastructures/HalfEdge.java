package bgtools.datastructures;

import org.lwjgl.BufferUtils;

import javax.vecmath.Vector3f;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * Half Edge Data structure
 * Created by brendan on 15-12-07.
 */
public class HalfEdge {

  private List<Face> faces;
  private HalfEdge parent;
  private boolean smoothBoundaryEdges = true;
  private FloatBuffer vertexBuffer;
  private FloatBuffer normalBuffer;
  private FloatBuffer faceNormalBuffer;


  public HalfEdge() {
    faces = new LinkedList<>();
  }

  public HalfEdge(List<Vector3f> vertices) {
    faces = new LinkedList<>();
    faces.add(new Face(vertices));
  }

  public HalfEdge(List<Face> faces, boolean alreadyJoined) {
    this.faces = faces;
    if (alreadyJoined) {
      return;
    }

    // Sort all possible brothers by hash cell
    // brother edges must have the same hash code
    HashMap<Integer, List<Edge>> brothers = new HashMap<>();
    for (Face f: faces) {
      for (Edge e: f.getEdges()) {
        Integer hash = e.hashCode();
        if (!brothers.containsKey(hash)) {
          brothers.put(hash, new ArrayList<>());
        }
        brothers.get(hash).add(e);
      }
    }

    // Should be quick since unless there's a hash collision
    // each edgeList should have only two elements
    for (List<Edge> edgeList: brothers.values()) {
      for (int i = 0; i < edgeList.size(); i++) {
        for (int j = i+1; j < edgeList.size(); j++) {
          Edge e1 = edgeList.get(i);
          Edge e2 = edgeList.get(j);
          if (e1.isBrother(e2)) {
            e1.brother = e2;
            e2.brother = e1;
          }
        }
      }
    }
  }


  public List<Face> getFaces() {
    return faces;
  }

  public Set<Vector3f> getFreshPoints() {
    Set<Vector3f> points = new HashSet<>();
    if (parent == null) {
      return points;
    }

    for (Face f: parent.faces) {
      Edge e = f.edge;
      do {
        points.add(e.getMidpoint());
        e = e.sister;
      } while (e != f.edge);
    }
    return points;
  }

  public HalfEdge smooth() {

    if (parent == null) {
      System.out.println("Warning: called smooth on structure with no parent");
      return this;
    }

    Set<Vector3f> adjusted = new HashSet<>();
    // average face points between their current position their expected position
    adjusted.clear();
    for (Face f: parent.faces) {
      Edge e = f.edge;
      Vector3f expected = f.getMidpoint();
      do {
        // don't adjust if already adjusted
        Vector3f midpoint = e.son.sister.v2;
        if (adjusted.contains(midpoint)) {
          e = e.sister;
          continue;
        }
        midpoint.set(expected);
        e = e.sister;
      } while (e != f.edge);
    }


    // set each new edge point to be average of
    // two neighboring face points and end points
    adjusted.clear();
    for (Face f: parent.faces) {
      Edge e = f.edge;
      do {
        Vector3f midpoint = e.getMidpoint();
        // don't adjust if already adjusted or if boundary
        if (adjusted.contains(midpoint)) {
          e = e.sister;
          continue;
        }

        adjusted.add(midpoint);

        if (e.brother != null) {
          Vector3f avg = new Vector3f();
          avg.add(e.v1, e.v2);
          avg.scale(3f / 8f);
          avg.scaleAdd(1f / 16f, e.sister.v2, avg);
          avg.scaleAdd(1f / 16f, getReverseSister(e).v1, avg);
          avg.scaleAdd(1f / 16f, e.brother.sister.v2, avg);
          avg.scaleAdd(1f / 16f, e.brother.sister.v2, avg);
          avg.scaleAdd(1f / 16f, getReverseSister(e.brother).v1, avg);
          midpoint.set(avg);
        } else {
          // TODO look into this (could be cause of problem)
//          Vector3f avg = e.v1.add(e.v2).mult(3f/8f);
//					avg = avg.addMult(e.sister.v2, 1f/8f);
//					avg = avg.addMult(getReverseSister(e).v1, 1f/16f);
//          avg = e.v1.add(e.v2).mult(0.5f);

          Vector3f avg = new Vector3f();
          avg.add(e.v1, e.v2);
          avg.scale(3f/8f);
          midpoint.set(avg);
        }

        e = e.sister;
      } while (e != f.edge);
    }

    adjusted.clear();
    for (Face f: parent.faces) {
      Edge e = f.edge;
      do {
        // don't adjust if already adjusted
        if (adjusted.contains(e.v1)) {
          e = e.sister;
          continue;
        }
        smoothOldVertex(e);
        e = e.sister;
      } while (e != f.edge);
    }
    return this;
  }

  public HalfEdge subdivide() {

    HalfEdge subdivided = new HalfEdge();
    for (Face f: faces) {
      Vector3f faceMidpoint = f.getMidpoint();
      Edge prev = f.edge;
      Edge next = prev.sister;

      // create faces
      do {
        subdivided.faces.add(makeFace(prev, next, faceMidpoint));
        prev = prev.sister;
        next = next.sister;
      } while (prev != f.edge);

      // connect brothers between new faces
      do {
        prev.son.sister.brother = prev.daughter.face.edge;
        prev.daughter.face.edge.brother = prev.son.sister;
        prev = prev.sister;
      } while (prev != f.edge);
    }

    // connect brothers
    for (Face f: faces) {
      Edge edge = f.edge;
      do {
        if (edge.brother != null) {
          edge.son.brother = edge.brother.daughter;
          edge.daughter.brother = edge.brother.son;
        }
        edge = edge.sister;
      } while (edge != f.edge);
    }
    subdivided.parent = this;
    return subdivided;
  }


  private Edge getReverseSister(Edge edge) {
    Edge e = edge;
    while (e.sister != edge) {
      e = e.sister;
    }
    return e;
  }

  private void smoothOldVertex(Edge edge) {

    Vector3f faceAvg = new Vector3f();
    int n = 0;

    Edge e = edge;
    do {
      faceAvg.add(e.sister.v2);
      e = e.brother != null ? e.brother.sister : null;
      n++;
    } while (e != null && e != edge);
    faceAvg.scale(1f / 64f);

    // smooth by using boundary edges only
    if (e == null) {
      if (!smoothBoundaryEdges) {
        return;
      }

      Edge last = edge;
      while (last.brother != null) {
        last = last.brother.sister;
      }

      Edge first = edge;
      Edge hanging = getReverseSister(first);
      while (hanging.brother != null) {
        first = hanging.brother;
        hanging = getReverseSister(first);
      }
      Vector3f smoothed = new Vector3f();
      smoothed.add(last.son.v2, hanging.son.v2);
      smoothed.scale(1f / 3f);
      smoothed.scaleAdd(1f / 3f, edge.v1, smoothed);
      return;
    }

    // TODO not dividing by n?
    // set each original point to (F + 2R + (n-3)P) / n
    Vector3f edgeAvg = new Vector3f();
    e = edge;
    do {
      edgeAvg.add(e.son.v2);
      e = e.brother != null ? e.brother.sister : null;
    } while (e != null && e != edge);
    edgeAvg.scale(6f / 64f);

    Vector3f smoothed = new Vector3f();
    smoothed.add(edgeAvg, faceAvg);
    smoothed.scaleAdd(36f / 64f, edge.v1, smoothed);
    edge.v1.set(smoothed);

  }

  public HalfEdge mountain(float min, float max, float edgeMin, float edgeMax) {
    HalfEdge child = subdivide();

    for (Face f: faces) {
      // move face midpoint
      f.edge.son.sister.v2.y += (float) Math.random() * (max - min) + min;

      // move all edge points
      Edge e = f.edge;
      do {
        e.son.v2.y += (float) Math.random() * (edgeMax - edgeMin) + edgeMin;
        e = e.sister;
      } while (e != f.edge);
    }
    return child;
  }

  public FloatBuffer getVertexBuffer() {
    if (vertexBuffer != null) {
      return vertexBuffer;
    }
    vertexBuffer = BufferUtils.createFloatBuffer(18 * faces.size());
    normalBuffer = BufferUtils.createFloatBuffer(18 * faces.size());

    for(Face f: faces) {
      Vector3f tmp = new Vector3f();
      tmp.sub(f.edge.v1, f.edge.sister.v2);
      float dis1 = tmp.length();
      tmp.sub(f.edge.v2, f.edge.sister.sister.v2);
      float dis2 = tmp.length();

      Vector3f a, b, c, d;
      Vector3f na, nb, nc, nd;
      // connect shorter distance
      if (dis1 < dis2) {
        a = f.edge.v1;
        na = getNormal(f.edge);
        b = f.edge.v2;
        nb = getNormal(f.edge.sister);
        c = f.edge.sister.sister.v2;
        nc = getNormal(f.edge.sister.sister.sister);
        d = f.edge.sister.v2;
        nd = getNormal(f.edge.sister.sister);
      } else {
        a = f.edge.v2;
        na = getNormal(f.edge.sister);
        b = f.edge.sister.v2;
        nb = getNormal(f.edge.sister.sister);
        c = f.edge.v1;
        nc = getNormal(f.edge);
        d = f.edge.sister.sister.v2;
        nd = getNormal(f.edge.sister.sister.sister);
      }

      vertexBuffer
          .put(a.x).put(a.y).put(a.z)
          .put(b.x).put(b.y).put(b.z)
          .put(c.x).put(c.y).put(c.z)
          .put(b.x).put(b.y).put(b.z)
          .put(d.x).put(d.y).put(d.z)
          .put(c.x).put(c.y).put(c.z);

      normalBuffer
          .put(na.x).put(na.y).put(na.z)
          .put(nb.x).put(nb.y).put(nb.z)
          .put(nc.x).put(nc.y).put(nc.z)
          .put(nb.x).put(nb.y).put(nb.z)
          .put(nd.x).put(nd.y).put(nd.z)
          .put(nc.x).put(nc.y).put(nc.z);
    }

    vertexBuffer.flip();
    normalBuffer.flip();
    return vertexBuffer;
  }

  public FloatBuffer getNormalBuffer() {
    if (normalBuffer != null) {
      return normalBuffer;
    }
    getVertexBuffer();
    return normalBuffer;
  }

  public FloatBuffer getFaceNormalBuffer() {
    if (faceNormalBuffer != null) {
      return faceNormalBuffer;
    }

    FloatBuffer vertexBuffer = getVertexBuffer();
    faceNormalBuffer = BufferUtils.createFloatBuffer(vertexBuffer.limit());

    for (int i = 0; i < vertexBuffer.limit(); i += 9) {
      Vector3f a = new Vector3f(vertexBuffer.get(i), vertexBuffer.get(i+1), vertexBuffer.get(i+2));
      Vector3f b = new Vector3f(vertexBuffer.get(i+3), vertexBuffer.get(i+4), vertexBuffer.get(i+5));
      Vector3f c = new Vector3f(vertexBuffer.get(i+6), vertexBuffer.get(i+7), vertexBuffer.get(i+8));
      Vector3f ab = new Vector3f();
      ab.scaleAdd(-1, a, b);
      Vector3f ac = new Vector3f();
      ac.scaleAdd(-1, a, c);
      Vector3f normal = new Vector3f();
      normal.cross(ab, ac);
      normal.normalize();

      faceNormalBuffer
          .put(normal.x).put(normal.y).put(normal.z)
          .put(normal.x).put(normal.y).put(normal.z)
          .put(normal.x).put(normal.y).put(normal.z);
    }

    faceNormalBuffer.flip();
    return faceNormalBuffer;
  }

  private float[] getColor(Vector3f t, boolean snowCapped) {

    float nx = (t.x+1) * 0.4f;
    float nz = (t.z+1) * 0.4f;
    float noiseValue = 0;

    float color[] = new float[3];
    if (t.y - 0.2f * noiseValue > 0.5f) {
      color[0] = 0.9f;
      color[1] = 0.9f;
      color[2] = 0.9f;
    } else if (t.y > 0) {
      color[0] = noiseValue + 0.15f;
      color[1] = noiseValue + 0.8f - t.y;
      color[2] = 0.2f*noiseValue + 0.15f;
    } else {
      color[0] = 0.15f*(noiseValue + 0.05f);
      color[1] = 0.2f*(noiseValue + 0.8f - t.y);
      color[2] = 0.25f*(noiseValue + 0.4f - 4*t.y);
    }

    // clamp all values
    color[0] = Math.max(Math.min(color[0], 1), 0);
    color[1] = Math.max(Math.min(color[1], 1), 0);
    color[2] = Math.max(Math.min(color[2], 1), 0);

    return color;
  }

  private Vector3f getNormal(Edge edge) {

    Edge last = edge;
    while (last.brother != null && last.brother.sister != edge) {
      last = last.brother.sister;
    }

    Vector3f normal = new Vector3f();
    int n = 0;

    if (last.brother != null) {
      Edge prev = edge;
      Edge next = prev.brother.sister;
      do {
        Vector3f ab = new Vector3f();
        ab.scaleAdd(-1, prev.v1, prev.v2);
        Vector3f ac = new Vector3f();
        ac.scaleAdd(-1, next.v1, next.v2);
        ac.cross(ac, ab);
        ac.normalize();
        normal.add(ac);
        prev = next;
        next = next.brother.sister;
        n++;
      } while (prev != edge);
      normal.scale(1f/n);
      return normal;

    } else { // boundary case
      Edge first = edge;
      Edge hanging = getReverseSister(first);
      while (hanging.brother != null) {
        first = hanging.brother;
        hanging = getReverseSister(first);
      }
      Edge prev = first;
      while (prev.brother != null) {
        Edge next = prev.brother.sister;
        Vector3f ab = new Vector3f();
        ab.scaleAdd(-1, prev.v1, prev.v2);
        Vector3f ac = new Vector3f();
        ac.scaleAdd(-1, next.v1, next.v2);
        ac.cross(ac, ab);
        ac.normalize();
        normal.add(ac);
        prev = next;
        n++;
      }
      Vector3f ab = new Vector3f();
      ab.scaleAdd(-1, hanging.v2, hanging.v1);
      Vector3f ac = new Vector3f();
      ac.scaleAdd(-1, first.v1, first.v2);
      ac.cross(ac, ab);
      ac.normalize();
      normal.add(ac);
      normal.scale(1f /(n+1));
      return normal;
    }
  }

  private Face makeFace(Edge e1, Edge e2, Vector3f faceVertex) {
    Vector3f m1 = e1.getMidpoint();
    Vector3f m2 = e2.getMidpoint();

    Edge s1 = new Edge(m1, e1.v2);
    Edge s2 = new Edge(e2.v1, m2);
    Edge s3 = new Edge(m2, faceVertex);
    Edge s4 = new Edge(faceVertex, m1);
    s1.sister = s2;
    s2.sister = s3;
    s3.sister = s4;
    s4.sister = s1;
    e1.daughter = s1;
    e2.son = s2;
    Face face = new Face(s4);
    s1.face = face;
    s2.face = face;
    s3.face = face;
    s4.face = face;
    return face;
  }


  private int hash(Vector3f v) {
    return new Float(v.x + v.y + v.z).hashCode();
  }

  public static class Edge {
    private Vector3f v1, v2, midpoint;
    private Face face;
    private Edge sister;
    private Edge brother;
    private Edge son, daughter;

    public Vector3f getV1() {
      return v1;
    }

    public Vector3f getV2() {
      return v1;
    }

    public Edge getBrother() {
      return brother;
    }

    public Edge getSister() {
      return sister;
    }

    public Face getFace() {
      return face;
    }

    public Edge(Vector3f v1, Vector3f v2) {
      this.v1 = v1;
      this.v2 = v2;
    }

    public boolean isBrother(Edge e) {
      return e.v1 == v2 && e.v2 == v1;
    }

    public Vector3f getMidpoint() {
      if (midpoint == null) {
        if (brother != null && brother.midpoint != null) {
          midpoint = brother.midpoint;
        } else {
          midpoint = new Vector3f();
          midpoint.add(v1, v2);
          midpoint.scale(0.5f);
        }
      }
      return midpoint;
    }

    public int hashCode() {
      return v1.hashCode() + v2.hashCode();
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof Edge)) {
        return false;
      }
      Edge e = (Edge) obj;
      return v1.equals(e.v1) && v2.equals(e.v2);
    }
  }

  public static class Face {
    private Edge edge;

    public Face(Edge edge) {
      this.edge = edge;
      edge.face = this;
    }

    public Face(List<Vector3f> vertices) {
      Iterator<Vector3f> iter = vertices.iterator();
      Vector3f a = iter.next();
      Vector3f b = iter.next();
      Edge prev = new Edge(a, b);
      edge = prev;
      while (iter.hasNext()) {
        a = b;
        b = iter.next();
        Edge next = new Edge(a, b);
        prev.sister = next;
        prev = next;
      }
      Edge next = new Edge(b, vertices.get(0));
      prev.sister = next;
      next.sister = edge;
    }

    public Edge getEdge() {
      return edge;
    }

    public Vector3f getMidpoint() {
      Vector3f midpoint = new Vector3f();
      Edge e = edge;
      int count = 0;
      do {
        midpoint.add(e.v2);
        e = e.sister;
        count++;
      } while (e != edge);
      midpoint.scale(1f / count);
      return midpoint;
    }

    public List<Edge> getEdges() {
      List<Edge> edges = new LinkedList<>();
      Edge e = edge;
      do {
        edges.add(e);
        e = e.sister;
      } while (e != edge);
      return edges;
    }

  }

}