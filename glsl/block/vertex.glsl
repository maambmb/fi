#version 400 core;

in vec3 av_position;
in int av_normal;
in int av_shadow;
in int av_block_type;
in int av_lighting_global;
in int av_lighting_constant;

uniform mat4 uv_view_matrix;
uniform mat4 uv_model_tsc_matrix;
uniform mat4 uv_model_rot_matrix;
uniform mat4 uv_projection_matrix;
uniform vec3 uv_light_source;
uniform vec3 uv_fog;
uniform vec3 uv_lighting_base;
uniform vec3 uv_lighting_global;
uniform vec3 uv_lighting_constant;
uniform float uv_tex_coords[ 2 * 3 ];

out vec3 out_lighting;
out vec3 out_normal;
out float out_shadow;

void main(void) {

    ivec3 lighting_constant_vec;
    ivec3 lighting_global_vec;
    float lighting_global_angle_factor;

    gl_Position  = uv_projection_matrix * uv_view_matrix * uv_model_tsc_matrix * uv_model_rot_matrix * vec4(position,1.0);
    out_normal   = vec3( av_normal & 0xFF, ( av_normal >> 8 ) & 0xFF, ( av_normal >> 16 ) & 0xFF );
    out_shaodw   = av_shadow;
    out_lighting = uv_lighting_ambient;

    lighting_global_angle_factor = 0.5f + max( dot( normalize( uv_lighting_global_position ), out_normal ), 0f ) / 2f;
    lighting_global_vec          = vec3( av_lighting_global & 0xFF, ( av_lighting_global >> 8 ) & 0xFF, ( av_lighting_global >> 16 ) & 0xFF ) / 255f;
    out_lighting                 = max( out_lighting, max( lighting_global_vec, uv_lighting_global ) * lighting_global_angle_factor );

    lighting_constant_vec = ivec3( av_lighting_constant & 0xFF, ( av_lighting_constant >> 8 ) & 0xFF, ( av_lighting_constant >> 16 ) & 0xFF ) / 255f;
    out_lighting          = max( out_lighting, max( lighting_constant_vec, uv_lighting_constant ) );

}

