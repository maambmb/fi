package game;

public class Config {

    // the length of a chunk cube in individual blocks 
	public static final int CHUNK_DIM   = 8;
    // the maximum distance a light source can propagate
    public static final int LIGHT_JUMPS = 8;
    // the number of bytes required for non-positional vertex data
    public static final int VBO_NONPOS_BYTES = 28;
    // the number of ints required to store the non-pos bytes
    public static final int VBO_NONPOS_INTS = (int)Math.ceil( VBO_NONPOS_BYTES / 4 );
    // the number of initial non-pos bytes reserved for non-lighting
    public static final int VBO_NONPOS_NONLIGHT_BYTES = 10;
	
}
