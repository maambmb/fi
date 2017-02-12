#version 400 core

in vec2 av_position_2d;
in vec2 av_tex_coords;

uniform mat4 uv_model;
uniform mat4 uv_projection;
uniform int uv_color;

out vec2 frag_tex_coords;
out vec3 frag_color;

void main(void) {

	gl_Position = uv_projection * uv_model * vec4( av_position_2d.xy, 0, 1.0 );

    // just pass the tex coords through *yawn*
    frag_tex_coords = vec2( av_tex_coords.xy );
    frag_color = vec3( ( uv_color >> 16 ) & 0xFF, ( uv_color >> 8 ) & 0xFF, uv_color  & 0xFF ) / 255.0;
}

