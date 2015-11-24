#version 400 core

in vec3 position;
in vec3 color;
in vec3 normal;

uniform mat4 transformation;
uniform mat4 invTransform;
uniform mat4 viewing;
uniform mat4 projection;

out vec3 sColor;
out vec3 sNormal;
out vec3 sPosition;

void main() {
    vec4 worldPos = transformation * vec4(position, 1.0);
    sNormal = (transpose(invTransform) * vec4(normal, 0.0)).xyz;
    sColor = color;
    sPosition = worldPos.xyz;
    gl_Position = projection * viewing * worldPos;
}