#version 140

uniform mat4 viewMatrix, modelMatrix, projectionMatrix;

in vec3 vertexPosition;
in vec3 vertexColor;

out vec3 outColor;

void main(){
	outColor = vertexColor;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix *  vec4(vertexPosition,1.0f) ;
}