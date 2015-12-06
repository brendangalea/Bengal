#version 400 core

in vec2 textureCoords;
in vec3 position;

out vec3 sColor;


void main() {
  gl_Position = vec4(position, 1.0);
  sColor = vec3(textureCoords.x, 1.0, textureCoords.y);
}