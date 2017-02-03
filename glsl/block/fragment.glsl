#version 400 core

in vec2 frag_tex_coords;
in vec3 frag_lighting;

uniform sampler2D textureSampler;

out vec4 out_Color;

void main(void) {

    // sample the texture to get the color
    vec3 tex_color = texture( textureSampler, frag_tex_coords ).xyz;

    // multiply the texture's color with the lighting to get the final pixel color
    vec3 final_color = tex_color * frag_lighting;

    // and boom we're done :-D
    out_Color = vec4(tex_color.xyz,1);

}
