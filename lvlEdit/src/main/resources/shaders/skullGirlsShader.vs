#version 140

uniform mat4 modelMatrix, viewMatrix, projectionMatrix;

in vec3 vertexPosition;

void main(){
    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0f);
}