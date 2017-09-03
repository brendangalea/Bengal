#version 400 core

#define OVERWRITE_DEPTH
//#define ANTIALIAS

const float KA = 0.1;
const float KD = 0.5;
const float KS = 4;
const vec3 LIGHT_DIRECTION = normalize(vec3(-1, 1, -1));
const vec3 LIGHT_POSITION = vec3(0, 10, -10);
const float EPSILON = 1e-6;

uniform vec3 viewer;

in float sRadius;
in vec3 sColor;
in vec3 sCenter;
in vec3 sPosition;
in mat4 sProjectionViewing;
in vec3 sToCenter;
in float sBlurRadius;

out vec4 out_Color;

void main() {

    vec3 u = sPosition - viewer;
    vec3 v = viewer - sCenter;

    float a = dot(u, u);
    float b = 2 * dot(v, u);
    float c = dot(v, v) - sRadius * sRadius;

    if (a < EPSILON || c < EPSILON) {
        out_Color = vec4(1.0, 0.1, 0.1, 1);
        discard;
    }

    float disc = b * b - 4 * a * c;

    if (disc < EPSILON) {
        out_Color = vec4(0.1, 0.1, 1.0, 1);
        discard;
    }

    float t = (-b - sqrt(disc)) / (2 * a);

    vec3 point = viewer + t * u;
    vec3 normal = normalize(point - sCenter);

    vec3 toLight = normalize(LIGHT_POSITION - point);
    float intensity = clamp(dot(toLight, normal), KA, 1.0);

    #ifdef ANTIALIAS
        float alpha = 1.0 - max((sqrt(disSquared) - sBlurRadius) / (sRadius - sBlurRadius), 0.0);
        out_Color = vec4(intensity, intensity, intensity, alpha);
    #else
        out_Color = vec4(sColor * intensity, 1.0);
    #endif

    #ifdef OVERWRITE_DEPTH
        vec4 position = sProjectionViewing * vec4(point, 1.0);
        gl_FragDepth = position.z / position.w;
    #endif

}