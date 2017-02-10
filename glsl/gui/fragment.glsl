#version 400 core

in vec2 frag_tex_coords;
in vec3 frag_color;

uniform sampler2D textureSampler;

out vec4 out_Color;

void main(void) {

    // sample the texture to get the color
    vec4 tex_color = texture( textureSampler, frag_tex_coords );
    
    if( tex_color.a < 0.5 ) {
    	discard;
    }

    // multiply the texture's color
    out_Color = tex_color * vec4( frag_color.xyz, 1 );

}
