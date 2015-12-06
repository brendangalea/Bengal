package rendering.components;

import java.nio.ByteBuffer;

/**
 *
 * Created by Brendan Galea on 11/26/2015.
 */
public class TextureData {

  private ByteBuffer buffer;
  private int width, height;

  public TextureData(ByteBuffer buffer, int width, int height) {
    this.buffer = buffer;
    this.width = width;
    this.height = height;
  }

  public ByteBuffer getBuffer() {
    return buffer;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
