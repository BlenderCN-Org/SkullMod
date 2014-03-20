 #version 150

 uniform mat4 viewMatrix, projMatrix;

 in vec3 position;
 in vec3 color;

 out vec3 Color;

 void main()
 {
    Color = color;
    gl_Position = projMatrix * viewMatrix * vec4(position,1.0f) ;
 }