#version 400 core

in vec3 sColor;
in vec3 sNormal;
in vec3 sPosition;

out vec4 out_Color;
void main() {
    vec3 lightPos = vec3(1, 2, -1.9);
    vec3 l = normalize(lightPos - sPosition);
    float brightness = 1.0;
    float ambient = 0.2;
    float intensity = min(max(brightness * dot(l, sNormal), 0) + ambient, 1.0);
    out_Color = vec4(intensity * sColor, 1.0);
}