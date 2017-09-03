#version 400 core

in vec2 textureCoords;
out vec4 out_Color;

uniform sampler2D colourTexture;

const float CONTRAST = 0.3;

void main(void){
	out_Color = texture(colourTexture, textureCoords);
	out_Color.rgb = (out_Color.rgb - 0.5) * (1.0 + CONTRAST) + 0.5;
}