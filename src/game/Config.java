package game;

public class Config {

    // the length of a chunk cube in individual blocks 
    public static final int CHUNK_DIM = 16;
    // the maximum distance a light source can propagate
    public static final int LIGHT_JUMPS = 12;
    // amount to decrease light per propagation
    public static final int LIGHT_DROPOFF = 256 / LIGHT_JUMPS;
    // furthest distance to draw
    public static final float FAR_PLANE = 1000f;
    // closest distance to draw
    public static final float NEAR_PLANE = 0.1f;
    // block texture atlas path
    public static final String BLOCK_ATLAS = "tex/block.png";
    // single block face texture size
    public static final int BLOCK_ATLAS_TEX_DIM = 32;
    // gfx versions
    public static final int OPENGL_VERSION = 3;
    public static final int LWJGL_VERSION = 2;
    // games field of view (keep relative wide for dat anti-nausea)
    public static float FIELD_OF_VIEW = 90;
    // fps cap (vsync)
    public static int FPS = 60;
    // x*y of the window
    public static int GAME_WIDTH  = 800;
    public static int GAME_HEIGHT = 600;

}