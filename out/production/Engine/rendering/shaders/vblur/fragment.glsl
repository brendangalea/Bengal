#version 400 core

in vec2 sBlurTextureCoords[11];

out vec4 out_Color;

uniform sampler2D originalTexture;

void main() {
    out_Color = vec4(0.0);
    out_Color += texture(originalTexture, sBlurTextureCoords[0]) * 0.0093;
    out_Color += texture(originalTexture, sBlurTextureCoords[1]) * 0.028002;
    out_Color += texture(originalTexture, sBlurTextureCoords[2]) * 0.065984;
    out_Color += texture(originalTexture, sBlurTextureCoords[3]) * 0.121703;
    out_Color += texture(originalTexture, sBlurTextureCoords[4]) * 0.175713;
    out_Color += texture(originalTexture, sBlurTextureCoords[5]) * 0.198596;
    out_Color += texture(originalTexture, sBlurTextureCoords[6]) * 0.175713;
    out_Color += texture(originalTexture, sBlurTextureCoords[7]) * 0.121703;
    out_Color += texture(originalTexture, sBlurTextureCoords[8]) * 0.065984;
    out_Color += texture(originalTexture, sBlurTextureCoords[9]) * 0.028002;
    out_Color += texture(originalTexture, sBlurTextureCoords[10]) * 0.0093;
}
