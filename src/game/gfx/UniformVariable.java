package game.gfx;

public enum UniformVariable {

    MODEL( "uv_model" ),
    VIEW_MATRIX( "uv_view" ),
    PROJECTION_MATRIX( "uv_projection" ),
    LIGHT_ORIGIN( "uv_light_origin" ),
    FOG_COLOR( "uv_fog" ),
    LIGHTING_BASE( "uv_lighting_base" ),
    LIGHTING_GLOBAL( "uv_lighting_global" ),
    LIGHTING_CONSTANT( "uv_lighting_constant" ),
    COLOR( "uv_color"),
	MAX_DISTANCE("uv_max_distance");

    public String name;
    private UniformVariable( String name ) {
        this.name = name;
    }

}
