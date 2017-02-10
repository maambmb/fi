#version 400 core

in vec2 av_position_2d;
in vec2 av_tex_coords;

uniform mat4 uv_model_translate_scale;
uniform int uv_color;

out vec2 frag_tex_coords;
out vec3 frag_color;

void main(void) {

	vec4 transformed = uv_model_translate_scale * vec4( av_position_2d.xy, 0, 1.0 );
    gl_Position = vec4( transformed.x * 2 - 1 , 1 - transformed.y * 2, 0, 1.0 );

    // just pass the tex coords through *yawn*
    frag_tex_coords = vec2( av_tex_coords.xy );
    frag_color = vec3( uv_color & 0xFF, ( uv_color >> 8 ) & 0xFF, ( uv_color >> 16 ) & 0xFF ) / 255f;
}

