#version 400 core

const float ROOT_2 = 1.415;

in vec3 position;
in float radius;
in vec3 color;

uniform mat4 transformation;
uniform mat4 viewing;
uniform mat4 projection;
uniform vec3 viewer;

out float sRadius;
out vec3 sColor;
out vec3 sCenter;
out vec3 sPosition;
out mat4 sProjectionViewing;
out float sBlurRadius;


const vec2 pos[] = vec2[4](
  vec2(-1,  1),
  vec2(-1, -1),
  vec2(1,   1),
  vec2(1,  -1)
);

void main() {


    vec2 offset = pos[gl_VertexID];
    vec4 worldPos = transformation * vec4(position, 1.0);
    sCenter = worldPos.xyz;
    vec3 toViewer = normalize(viewer - worldPos.xyz);

    vec3 perpendicular = vec3(1, 0, 0);
    if (toViewer.x != 0) {
        perpendicular = vec3(0, 1, 0);
    }
    vec3 up = normalize(cross(perpendicular, toViewer));
    vec3 left = normalize(cross(up, toViewer));
    vec3 shifted = worldPos.xyz + radius * offset.y * up + radius * offset.x * left;
    shifted += radius * toViewer;
    sPosition = shifted;
    sProjectionViewing = projection * viewing;
    gl_Position = sProjectionViewing * vec4(shifted, 1.0);

    // pass along attributes
    sColor = color;
    sRadius = radius;


    vec3 edge = worldPos.xyz + radius * left;
    float v = sqrt(dot(edge - viewer, edge - viewer));
    float u = sqrt(dot(viewer - worldPos.xyz, viewer - worldPos.xyz));
    sBlurRadius = u * tan(acos(u/v) - radians(0.1));


}