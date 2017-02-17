#version 400 core

in vec3 av_position;
in vec2 av_tex_coords;

uniform mat4 uv_view;
uniform mat4 uv_model;
uniform mat4 uv_projection;

uniform int uv_color;
uniform vec2 uv_texture_offset;

out vec2 frag_tex_coords;
out vec3 frag_color;

void main(void) {

    // get the final position by multiply the position by the model matrices -> view matrix -> proj matrix
    gl_Position = uv_projection ( uv_view * uv_model * vec4(av_position,1.0);
    
    // set final lighting to the base ambient amount
    frag_color      = vec3( ( uv_color >> 16 ) & 0xFF, ( uv_color >> 8 ) & 0xFF, uv_color & 0xFF ) / 255.0;
    frag_tex_coords = av_tex_coords + uv_texture_offset;
    
}

