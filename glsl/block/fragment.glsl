#version 400 core

in vec2 frag_tex_coords;
in vec3 frag_lighting;
in float frag_fog;

uniform int uv_fog;
uniform sampler2D textureSampler;

out vec4 out_Color;

void main(void) {

	vec4 tex_color;
	vec4 lit_color;
	vec4 fog_color;
	
	fog_color = vec4( ( uv_fog >> 16 ) & 0xFF, ( uv_fog >> 8 ) & 0xFF, uv_fog & 0xFF, 255f ) / 255f;

    // sample the texture to get the color
    tex_color = texture( textureSampler, frag_tex_coords );
    
    if( tex_color.a < 0.5 ) {
    	discard;
    }
    // multiply the texture's color with the lighting to get the final pixel color
    lit_color = tex_color * vec4(frag_lighting.xyz,1);
    out_Color = lit_color * ( 1 - frag_fog ) + frag_fog * fog_color;
}
