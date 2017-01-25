package game.gfx;

public enum UniformVariable {

	MODEL_TRANSLATE_SCALE_MATRIX( "uv_model_tsc_matrix" ),
	MODEL_ROTATE_MATRIX( "uv_model_rot_matrix" ),
	VIEW_MATRIX( "uv_view_matrix" ),
	PROJECTION_MATRIX( "uv_projection_matrix" ),
	LIGHTING_GLOBAL( "uv_lighting_global" ),
	LIGHTING_CONSTANT( "uv_lighting_constant" );

    public String name;
    private UniformVariable( String name ) {
        this.name = name;
    }
	
}
