package game;

import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import game.block.World;
import game.gfx.AtlasLoader;
import game.gfx.shader.BlockShader;
import game.listener.Listener;

import util.Vector3fl;
import util.Vector3in;

public class Game {

    public class DestroyMessage {}

    public class UpdateMessage {

        public long deltaMs;
        public UpdateMessage( long deltaMs ) {
            this.deltaMs = deltaMs;
        }

    }

    private boolean gameOver;

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
        Vector3fl fogColor = Environment.ENV.fogColor;
        GL11.glClearColor( fogColor.x, fogColor.y, fogColor.z, 1 );
    }

    private void destroyCtx() {
        Display.destroy();
    }

    public void run() {

        // fill the cubenorml enum with extra meta information
        Vector3in.CubeNormal.init();
        // create the global msg listener
        Listener.init();
        // create the world object (manager of chunks)
        World.init();

        // create global entities
        BlockShader.init();
        Camera.init();
        Environment.init();
        AtlasLoader.init();

        // set up display ctx
        this.createCtx();

        // loop until we detect a close (done in updateCtx)
        while( !this.gameOver ) {
            this.prepareCtx();

            // first perform an update, and then a shader prepare + shader render for the BLOCK shader
            Listener.GLOBAL_LISTENER.listen( new UpdateMessage( 0 ) );
            Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderPrepareMessage() );
            Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderRenderMessage() );

            this.updateCtx();
        }

        // if we've exited the loop, the game is about to end. Try and clean up all resources by destroying all resources
        Listener.GLOBAL_LISTENER.listen( new DestroyMessage() );

        // finally destroy the display ctx
        this.destroyCtx();

    }

}
