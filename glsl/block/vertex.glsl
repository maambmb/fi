#version 400 core

in vec3 av_position;
in vec2 av_tex_coords;
in int av_normal;
in int av_shadow;
in int av_lighting_global;
in int av_lighting_constant;

uniform mat4 uv_view_matrix;
uniform mat4 uv_model_tsc_matrix;
uniform mat4 uv_model_rot_matrix;
uniform mat4 uv_projection_matrix;
uniform vec3 uv_global_light_origin;
uniform vec3 uv_fog;
uniform vec3 uv_lighting_base;
uniform vec3 uv_lighting_global;
uniform vec3 uv_lighting_constant;

out vec2 frag_tex_coords;
out vec3 frag_lighting;
out float frag_shadow;

void main(void) {

    vec3 lighting_constant_vec;
    vec3 lighting_global_vec;
    vec3 normal_vec;
    float lighting_global_angle_factor;
    float shadow_attenuation;

    // maximum amount a shadow can reduce illumination
    shadow_attenuation = 0.8f; 

    // 3 is the max number for av_shadow by design
    shadow_attenuation *= av_shadow / 3.0;

    // calculate the normal vector by unpacking the bytes of av_normal
    normal_vec = vec3( av_normal & 0xFF, ( av_normal >> 8 ) & 0xFF, ( av_normal >> 16 ) & 0xFF );

    // get the final position by multiply the position by the model matrices -> view matrix -> proj matrix
    gl_Position = uv_projection_matrix * uv_view_matrix * uv_model_tsc_matrix * uv_model_rot_matrix * vec4(av_position,1.0);

    // set final lighting to the base ambient amount
    frag_lighting = uv_lighting_base;

    // calculate the amount we should attenuate global lighting based on angle of the face (use normal_vec )
    //lighting_global_angle_factor = 0.5f + max( dot( normalize( uv_global_light_origin ), normal_vec  ), 0f ) / 2f;
    // unpack the global lighting vector and convert into a float vec [0f-1f]
    lighting_global_vec = vec3( av_lighting_global & 0xFF, ( av_lighting_global >> 8 ) & 0xFF, ( av_lighting_global >> 16 ) & 0xFF ) / 255.0;
    // join the *attenuated* global lighting with the current frag_lighting value using max
    frag_lighting = max( frag_lighting, max( lighting_global_vec, uv_lighting_global ) * lighting_global_angle_factor );

    // we now repeat the procedure with all other light sources (minus the angle attenuation)
    lighting_constant_vec = ivec3( av_lighting_constant & 0xFF, ( av_lighting_constant >> 8 ) & 0xFF, ( av_lighting_constant >> 16 ) & 0xFF ) / 255.0;
    frag_lighting = max( frag_lighting, max( lighting_constant_vec, uv_lighting_constant ) );

    // once all light sources have been join in, attenuate the final signal with the shadow
    frag_lighting *= ( 1 - shadow_attenuation );

    // just pass the tex coords through *yawn*
    frag_tex_coords = av_tex_coords;

}

