package game.gfx;

public enum AttributeVariable {

    POSITION( "av_position", 3, Float.class ),
    POSITION_2D( "av_position_2d", 2, Float.class ),
    COLOR( "av_color", 1, Integer.class ),
    TEX_COORDS( "av_tex_coords", 2, Float.class ),
    NORMAL( "av_normal", 1, Integer.class ),
    SHADOW( "av_shadow", 1, Integer.class ),
    LIGHTING_GLOBAL( "av_lighting_global", 1, Integer.class ),
    LIGHTING_CONSTANT( "av_lighting_constant", 1, Integer.class );

    public String name;
    public int stride;
    public Class<?> dataType;

    public static int TOTAL_WIDTH;

    private AttributeVariable( String name, int stride, Class<?> dataType ) {
        this.name = name;
        this.stride = stride;
        this.dataType = dataType;
    }


}
