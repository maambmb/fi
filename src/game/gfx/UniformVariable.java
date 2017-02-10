package game.gfx;

public enum UniformVariable {

    MODEL_TRANSLATE_SCALE_MATRIX( "uv_model_translate_scale" ),
    MODEL_ROTATE_MATRIX( "uv_model_rotate" ),
    VIEW_MATRIX( "uv_view" ),
    PROJECTION_MATRIX( "uv_projection" ),
    GLOBAL_LIGHT_ORIGIN( "uv_global_light_origin" ),
    FOG_COLOR( "uv_fog" ),
    LIGHTING_BASE( "uv_lighting_base" ),
    LIGHTING_GLOBAL( "uv_lighting_global" ),
    LIGHTING_CONSTANT( "uv_lighting_constant" ),
    COLOR( "uv_color");

    public String name;
    private UniformVariable( String name ) {
        this.name = name;
    }

}
