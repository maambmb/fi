package game.block;

import game.gfx.AttributeVariable;

// the various light sources in the game
// each light source can have multiple lights, but
// they share a common modulator/multiplier
public enum LightSource {

    GLOBAL( AttributeVariable.LIGHTING_GLOBAL ),
    CONSTANT( AttributeVariable.LIGHTING_CONSTANT );

	public AttributeVariable attributeVariable;
	private LightSource( AttributeVariable attributeListVariable ) {
		this.attributeVariable = attributeListVariable;
	}

}

