#version 400 core

in vec2 position;
out vec2 sBlurTextureCoords[11];

uniform float radius;

void main() {

    gl_Position = vec4(position, 0.0, 1.0);
    vec2 centerTexCoords = position * 0.5 + 0.5;
    float pixelSize = 1.0 / radius;

    for (int i = -5; i <= 5; i++) {
        sBlurTextureCoords[i+5] = centerTexCoords + vec2(0.0, pixelSize * i);
    }
}
