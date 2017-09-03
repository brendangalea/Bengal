#version 400

in vec3 position;
out vec3 textureCoords;

uniform mat4 projection;
uniform mat4 viewing;

void main(void){

	mat4 noTranslation = viewing;
	noTranslation[3][0] = 0;
	noTranslation[3][1] = 0;
	noTranslation[3][2] = 0;

	gl_Position = projection * noTranslation * vec4(position, 1.0);
	textureCoords = position;
	
}