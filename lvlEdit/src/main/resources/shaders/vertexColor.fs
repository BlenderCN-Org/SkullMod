#version 140

in vec3 outColor;
out vec4 fragmentColor;

void main(){
	fragmentColor = vec4(outColor,1.0);
}