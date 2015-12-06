#version 400 core



const vec3 lightPos = vec3(1, 2, -1.9);
in vec3 sNormal;
in vec3 sPosition;
in vec2 sTextureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;

void main() {
  vec3 l = normalize(lightPos - sPosition);
  float brightness = 1.0;
  float ambient = 0.2;
  float intensity = min(max(brightness * dot(l, sNormal), 0) + ambient, 1.0);

  vec4 color = texture(textureSampler, sTextureCoords);
  out_Color = vec4(color.rgb * intensity, 1.0);
}