package game;

public class Config {

    public static final int CHUNK_DIM = 8;
    public static final int LIGHT_JUMPS = 8;
    public static final int LIGHT_DROPOFF = 256 / LIGHT_JUMPS;

    public static final float NEAR_PLANE = 0.1f;

    public static final int OPENGL_VERSION = 3;
    public static final int LWJGL_VERSION = 3;

    public static final int FPS = 60;
    public static final int KEY_LOCK_MS = 600;

    public static float FIELD_OF_VIEW = 90;
    public static float ASPECT_RATIO = 16f/9f;
    public static int GAME_WIDTH  = 1600;
    public static int GAME_HEIGHT = (int)( GAME_WIDTH/ASPECT_RATIO );
}