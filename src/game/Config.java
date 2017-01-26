package game;

public class Config {

    // the length of a chunk cube in individual blocks 
    public static final int CHUNK_DIM = 8;
    // the maximum distance a light source can propagate
    public static final int LIGHT_JUMPS = 8;
    // amount to decrease light per propagation
    public static final int LIGHT_DROPOFF = 256 / LIGHT_JUMPS;
    // furthest distance to draw
    public static final float FAR_PLANE = 1000f;
    // closest distance to draw
    public static final float NEAR_PLANE = 0.1f;
    // texture atlas path
    public static final String BLOCK_ATLAS = "tex/block/atlas.png";
    // games field of view (keep relative wide for dat anti-nausea)
    public static float FIELD_OF_VIEW = 110;
    // x*y of the window
    public static int GAME_WIDTH  = 800;
    public static int GAME_HEIGHT = 600;

}
