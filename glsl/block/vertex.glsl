#version 400 core

in vec3 av_position;
in vec2 av_tex_coords;
in int av_normal;
in int av_shadow;
in int av_lighting_global;
in int av_lighting_constant;

uniform mat4 uv_view;
uniform mat4 uv_model;
uniform mat4 uv_projection;
uniform vec3 uv_light_origin;

uniform int uv_lighting_base;
uniform int uv_lighting_global;
uniform int uv_lighting_constant;
uniform float uv_max_distance;

out vec2 frag_tex_coords;
out vec3 frag_lighting;
out float frag_fog;
out float frag_shadow;

void main(void) {

	vec3 lighting_base;
    vec3 lighting_constant;
    vec3 lighting_constant_mod;
    vec3 lighting_global;
    vec3 lighting_global_mod;
    vec4 raw_pos;
    vec3 normal;

    float lighting_angle_factor;

    // get the final position by multiply the position by the model matrices -> view matrix -> proj matrix
    raw_pos = uv_view * uv_model * vec4(av_position,1.0);
    gl_Position = uv_projection * raw_pos;
    
    if( av_normal == 0 ) {
    	lighting_angle_factor = 1.0;
    }
    else {
    	normal = vec3( ( av_normal >> 16 ) & 0xFF, ( av_normal >> 8 ) & 0xFF, av_normal & 0xFF );
		// calculate the amount we should attenuate global lighting based on angle of the face (use normal_vec )
		lighting_angle_factor = 0.75 + dot( normalize( uv_light_origin ), normalize( normal ) ) / 4.0;
    }
    
    // set final lighting to the base ambient amount
    lighting_base = vec3( ( uv_lighting_base >> 16 ) & 0xFF, ( uv_lighting_base >> 8 ) & 0xFF, uv_lighting_base & 0xFF ) / 255.0;
    frag_lighting = lighting_base * lighting_angle_factor;

    // unpack the global lighting vector and convert into a float vec [0f-1f]
    lighting_global     = vec3( ( av_lighting_global >> 16 ) & 0xFF, ( av_lighting_global >> 8 ) & 0xFF, av_lighting_global & 0xFF ) / 255.0;
    lighting_global_mod = vec3( ( uv_lighting_global >> 16 ) & 0xFF, ( uv_lighting_global >> 8 ) & 0xFF, uv_lighting_global & 0xFF ) / 255.0;
    frag_lighting = max( frag_lighting, min( lighting_global, lighting_global_mod ) );

    // we now repeat the procedure with all other light sources (minus the angle attenuation)
    lighting_constant     = vec3( ( av_lighting_constant >> 16 ) & 0xFF, ( av_lighting_constant >> 8 ) & 0xFF, av_lighting_constant & 0xFF ) / 255.0;
    lighting_constant_mod = vec3( ( uv_lighting_constant >> 16 ) & 0xFF, ( uv_lighting_constant >> 8 ) & 0xFF, uv_lighting_constant & 0xFF ) / 255.0;
    frag_lighting = max( frag_lighting, min( lighting_constant, lighting_constant_mod ) );

    // once all light sources have been join in, attenuate the final signal with the shadow
    frag_lighting *= ( 1.0 - 0.25 * float(av_shadow) / 3.0 );
    frag_fog = length( raw_pos ) / uv_max_distance;

    // just pass the tex coords through *yawn*
    frag_tex_coords = av_tex_coords;

}

