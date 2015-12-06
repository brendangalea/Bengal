#version 400 core

in vec3 sColor;

out vec4 outColor;

void main() {
  outColor = vec4(sColor, 1.0);
}
