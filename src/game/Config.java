package game;

public class Config {

    // the length of a chunk cube in individual blocks 
	public static final int CHUNK_DIM   = 8;
    // the maximum distance a light source can propagate
    public static final int LIGHT_JUMPS = 8;
    // amount to decrease light per propagation
    public static final int LIGHT_DROPOFF = 256 / LIGHT_JUMPS;
    // the number of bytes required for non-positional vertex data
    public static final int BLOCK_VBO_NONPOS_BYTES = 28;
    // the number of initial non-pos bytes reserved for non-lighting
    public static final int BLOCK_VBO_NONPOS_NONLIGHT_BYTES = 10;
    // the number of ints required to store the non-pos bytes
    public static final int BLOCK_VBO_NONPOS_INTS = (int)Math.ceil( BLOCK_VBO_NONPOS_BYTES / 4 );

    public static float FAR_PLANE     = 1000f;
    public static float NEAR_PLANE    = 0.1f;
    public static float FIELD_OF_VIEW = 110;
    public static int GAME_WIDTH      = 800;
    public static int GAME_HEIGHT     = 600;

}
