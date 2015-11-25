package rendering.components;


/**
 * An object that can be rendered
 *
 * Created by brendan on 15-07-11.
 */
public interface Renderable {

  /**
   * @return The id of the vao this object is using
   */
  int getVaoId();

  /**
   * Load Uniforms and bind textures specific to this object,
   *  then call rendering code specific to this object
   *
   * @param shader The shader in current use
   */
  void draw(ShaderProgram shader);

}