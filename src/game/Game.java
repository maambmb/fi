package game;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import game.block.BlockShader;
import game.block.World;
import game.gfx.TextureRef;
import game.gui.FontMap;
import game.gui.GUIDepth;
import game.gui.GUIShader;
import game.gui.Glyph;
import game.gui.console.DebugConsole;
import game.input.InputCapturer;
import game.input.Key;
import util.Vector3fl;
import util.Vector3in;

public class Game {

    public class DestroyMessage {}

    public static class UpdateMessage {

        public long totalMs;
        public long deltaMs;

        public UpdateMessage( long prevTime, long currTime ) {
            this.deltaMs = currTime - prevTime;
            this.totalMs = currTime;
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
    
    private void destroyCtx() {
        Display.destroy();
    }

    public void run() {

        Mouse.setGrabbed( true );
        // set up display ctx
        this.createCtx();

        // record the start of the game
        this.prevTime = System.currentTimeMillis();

        // create the global msg listener
        Listener.init();

        // fill the cubenorml enum with extra meta information
        Vector3in.CubeNormal.init();
        Key.init();
        TextureRef.init();
        Glyph.init();
        FontMap.init();
        
        // create the world object (manager of chunks)
        World.init();

        // create global entities
        InputCapturer.init();
        BlockShader.init();
        GUIShader.init();
        Camera.init();
        DebugConsole.init();
        Environment.init();

        new RandomBlockSpawner();

        // loop until we detect a close (done in updateCtx)
        while( !this.gameOver ) {
        	
        	// do top level input checks (escape should quit
            if( InputCapturer.GLOBAL.isKeyDown( Key.KEY_ESCAPE ))
            	this.gameOver = true;
            if( InputCapturer.GLOBAL.isKeyPressed( Key.KEY_GRAVE ))
            	DebugConsole.GLOBAL.toggle();

            // take a new reading of the time, and create an update message with this and the previous reading
            // (to form a delta timestep) - then set this new reading as the previous one so we're
            // ready for the next loop iteration
            this.currTime = System.currentTimeMillis();
            Listener.GLOBAL.listen( new UpdateMessage( this.prevTime, this.currTime ) );
            this.prevTime = this.currTime;

			GL11.glEnable( GL11.GL_DEPTH_TEST );
			GL11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT );
			GL11.glEnable( GL11.GL_CULL_FACE );

			Vector3fl fogColor = Environment.GLOBAL.fogColor.toVector3fl().divide( 0xFF );
			GL11.glClearColor( fogColor.x, fogColor.y, fogColor.z, 1 );

            // then perform draws for all the various shader programs
            BlockShader.GLOBAL.use();
            Listener.GLOBAL.listen( new BlockShader.BlockShaderPreRenderMessage() );
            Listener.GLOBAL.listen( new BlockShader.BlockShaderRenderMessage() );

			GL11.glDisable( GL11.GL_DEPTH_TEST );

            GUIShader.GLOBAL.use();
            for( GUIDepth d : GUIDepth.values() )
				Listener.GLOBAL.listen( new GUIShader.GUIShaderRenderMessage( d ) );

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
