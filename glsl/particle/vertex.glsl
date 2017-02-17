#version 400 core

in vec3 av_position;
in vec3 av_position_world;
in vec2 av_tex_coords;
in int av_color;

uniform mat4 uv_view;
uniform mat4 uv_projection;
uniform mat4 uv_view_unrotator;

out vec2 frag_tex_coords;
out vec3 frag_color;

void main(void) {

    vec4 rotated_pos = ( uv_view_unrotator * vec4( av_position, 1.0 ) );
    rotated_pos += vec4( av_position_world.xyz, 0.0 );

    gl_Position = uv_projection * uv_view * rotated_pos;
    
    // set final lighting to the base ambient amount
    frag_color      = vec3( ( av_color >> 16 ) & 0xFF, ( av_color >> 8 ) & 0xFF, av_color & 0xFF ) / 255.0;
    frag_tex_coords = av_tex_coords;
    
}

