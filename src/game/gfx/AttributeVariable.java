package game.gfx;

public enum AttributeVariable {
	
	NORMAL( "av_normal" ),
	SHADOW( "av_shadow" ),
	BLOCK_TYPE( "av_block_type" ),
	LIGHTING_GLOBAL( "av_lighting_global" ),
	LIGHTING_CONSTANT( "av_lighting_constant" );

    public String name;
    private AttributeVariable( String name ) {
        this.name = name;
    }
	
}
