#version 400 core

in vec3 position;
in vec3 normal;
in vec2 textureCoords;

uniform mat4 transformation;
uniform mat4 invTransform;
uniform mat4 viewing;
uniform mat4 projection;

out vec3 sNormal;
out vec3 sPosition;
out vec2 sTextureCoords;

void main() {
  vec4 worldPos = transformation * vec4(position, 1.0);
  sNormal = (transpose(invTransform) * vec4(normal, 0.0)).xyz;
  sPosition = worldPos.xyz;
  gl_Position = projection * viewing * worldPos;
  sTextureCoords = textureCoords;
}