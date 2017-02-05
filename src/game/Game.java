package game;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import game.block.World;
import game.gfx.AtlasLoader;
import game.gfx.shader.BlockShader;
import util.Vector3fl;
import util.Vector3in;

public class Game {

    public class DestroyMessage {}

    public static class UpdateMessage {

        public long deltaMs;

        public UpdateMessage( long prevTime, long currTime ) {

            this.deltaMs = currTime - prevTime;

        }

    }

    private boolean gameOver;
    private long prevTime;
    private long currTime;

    private void createCtx() {
        ContextAttribs attribs = new ContextAttribs( Config.OPENGL_VERSION, Config.LWJGL_VERSION )
            .withForwardCompatible( true )
            .withProfileCore( true );
        try {
            Display.setDisplayMode( new DisplayMode( Config.GAME_WIDTH, Config.GAME_HEIGHT ) );
            Display.create( new PixelFormat(), attribs );
        } catch( Exception e ) {
            throw new RuntimeException( "Unable to create display" );
        }
    }

    private void updateCtx() {
        Display.sync( Config.FPS );
        Display.update();
        if( Display.isCloseRequested() )
            this.gameOver = true;
    }

    private void prepareCtx() {
        GL11.glEnable( GL11.GL_DEPTH_TEST );
        GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
        GL11.glEnable( GL11.GL_CULL_FACE );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST );
        Vector3fl fogColor = Environment.GLOBAL.fogColor;
        GL11.glClearColor( fogColor.x, fogColor.y, fogColor.z, 1 );
    }

    private void destroyCtx() {
        Display.destroy();
    }

    public void run() {

        Mouse.setGrabbed( true );
        // set up display ctx
        this.createCtx();

        // record the start of the game
        this.prevTime = System.currentTimeMillis();

        // fill the cubenorml enum with extra meta information
        Vector3in.CubeNormal.init();
        // create the global msg listener
        Listener.init();
        // create the world object (manager of chunks)
        World.init();

        // create global entities
        InputArbiter.init();
        BlockShader.init();
        Camera.init();
        Environment.init();
        AtlasLoader.init();

        new RandomBlockSpawner();

        // loop until we detect a close (done in updateCtx)
        while( !this.gameOver ) {
            this.prepareCtx();

            // take a new reading of the time, and create an update message with this and the previous reading
            // (to form a delta timestep) - then set this new reading as the previous one so we're
            // ready for the next loop iteration
            this.currTime = System.currentTimeMillis();
            Listener.GLOBAL.listen( new UpdateMessage( this.prevTime, this.currTime ) );
            this.prevTime = this.currTime;

            // then perform draws for all the various shader programs
            Listener.GLOBAL.listen( new BlockShader.BlockShaderPreRenderMessage() );
            Listener.GLOBAL.listen( new BlockShader.BlockShaderRenderMessage() );
            Listener.GLOBAL.listen( new BlockShader.BlockShaderPreRenderMessage() );

            World.WORLD.refresh();
            this.updateCtx();
            this.prevTime = this.currTime;
        }
        

        // if we've exited the loop, the game is about to end. Try and clean up all resources by destroying all resources
        Listener.GLOBAL.listen( new DestroyMessage() );

        // finally destroy the display ctx
        this.destroyCtx();

    }
    
    public static void main( String[] args ) {
        Game g = new Game();
        g.run();
        System.out.println("Game Terminated");
    }

}
