package rendering.utils;

import org.lwjgl.BufferUtils;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Parser for wavefront objects is very basic
 * Created by brendan on 15-12-06.
 */
public class WavefrontObject {


  private static final Pattern SPACE_SPLITTER = Pattern.compile("\\s+");
  private static final Pattern FACE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d*))?(?:/(\\d+))?");


  private List<Vector2f> textureCoords = new ArrayList<>();
  private List<Vector3f> normals = new ArrayList<>();
  private List<Integer> indices = new ArrayList<>();

  private FloatBuffer vertexBuffer;
  private FloatBuffer textureBuffer;
  private FloatBuffer normalBuffer;
  private IntBuffer indexBuffer;


  public WavefrontObject(String filepath) throws IllegalArgumentException {

    List<Vector3f> vertices = new ArrayList<>();
    try (BufferedReader in = new BufferedReader(new FileReader(filepath))) {
      String line;
      while ((line = in.readLine()) != null) {
        String[] values = SPACE_SPLITTER.split(line.trim());
        if (values[0].equals("v")) {
          vertices.add(new Vector3f(
              Float.parseFloat(values[1]),
              Float.parseFloat(values[2]),
              Float.parseFloat(values[3])));
        } else if (values[0].equals("vt")) {
          textureCoords.add(new Vector2f(
              Float.parseFloat(values[1]),
              Float.parseFloat(values[2])));
        } else if (values[0].equals("vn")) {
          normals.add(new Vector3f(
              Float.parseFloat(values[1]),
              Float.parseFloat(values[2]),
              Float.parseFloat(values[3])));
        } else if (values[0].equals("f")) {
          break;
        }
      }

      vertexBuffer = BufferUtils.createFloatBuffer(3 * vertices.size());
      textureBuffer = BufferUtils.createFloatBuffer(2 * vertices.size());
      normalBuffer = BufferUtils.createFloatBuffer(3 * vertices.size());

      while (line != null) {
        String[] values = SPACE_SPLITTER.split(line.trim());
        if (!values[0].equals("f")) {
          line = in.readLine();
          continue;
        }


        processFaceVertex(values[1]);
        processFaceVertex(values[2]);
        processFaceVertex(values[3]);

        line = in.readLine();
      }

    } catch (IOException exception) {
      throw new IllegalArgumentException("File not found:" + filepath);
    }

    for (Vector3f v: vertices) {
      vertexBuffer.put(v.x).put(v.y).put(v.z);
    }

    indexBuffer = BufferUtils.createIntBuffer(indices.size());
    indices.stream().forEach(indexBuffer::put);


    vertexBuffer.flip();
    indexBuffer.flip();

  }

  private void processFaceVertex(String value) throws IllegalArgumentException {
    Matcher matcher = FACE_PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Unexpected format found for .obj file");
    }

    int vertexPointer = Integer.parseInt(matcher.group(1)) - 1;
    indices.add(vertexPointer);

    if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
      Vector2f textureCoord = textureCoords.get(Integer.parseInt(matcher.group(2)) - 1);
      textureBuffer.put(vertexPointer * 2, textureCoord.x);
      textureBuffer.put(vertexPointer * 2 + 1, 1 - textureCoord.y);
    }

    if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
      Vector3f normal = normals.get(Integer.parseInt(matcher.group(3)) - 1);
      normalBuffer.put(vertexPointer * 3, normal.x);
      normalBuffer.put(vertexPointer * 3 + 1, normal.y);
      normalBuffer.put(vertexPointer * 3 + 2, normal.z);
    }
  }


  public FloatBuffer getVertexBuffer() {
    return vertexBuffer;
  }

  public FloatBuffer getTextureBuffer() {
    return textureBuffer;
  }

  public FloatBuffer getNormalBuffer() {
    return normalBuffer;
  }

  public IntBuffer getIndexBuffer() {
    return indexBuffer;
  }

}
