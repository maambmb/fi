package game.block;

import game.gfx.AttributeVariable;
import game.gfx.UniformVariable;

public enum LightSource {

    // the various light sources in the game
    // each light source can have multiple lights, but
    // they share a common modulator/multiplier

    GLOBAL( AttributeVariable.LIGHTING_GLOBAL, UniformVariable.LIGHTING_GLOBAL ),
    CONSTANT( AttributeVariable.LIGHTING_CONSTANT, UniformVariable.LIGHTING_CONSTANT ),
    NIGHT( AttributeVariable.LIGHTING_NIGHT, UniformVariable.LIGHTING_NIGHT );

    public AttributeVariable attributeVariable;
    public UniformVariable uniformVariable; 

    private LightSource( AttributeVariable av, UniformVariable uv ) {
        this.attributeVariable = av;
        this.uniformVariable = uv;
    }

}

