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
import game.listener.Listener;

import util.Vector3fl;
import util.Vector3in;

public class Game {

    public class DestroyMessage {}

    public static class UpdateMessage {

        public enum Interval {
            
            // enums representing intervals of time
            // useful for allowing stuff to be run regularly
            // but not every loop iteration

            MS_100(100),
            MS_200(200),
            MS_500(500),
            MS_1000(1000),
            MS_2000(2000),
            MS_5000(5000);

            public int ms;

            private Interval( int ms ) {
                this.ms = ms;
            }
        }

        public long deltaMs;
        private boolean[] intervals;

        public boolean getInterval( Interval i ) {
            return this.intervals[ i.ordinal() ];
        }

        public UpdateMessage( long prevTime, long currTime ) {

            // calculate the delta timestep
            this.deltaMs   = currTime - prevTime;

            // create an array for each interval enum
            this.intervals = new boolean[ Interval.values().length ];

            // for each interval, check if the next occurrence of the interval
            // occurs in the range between prevTime and currenTime
            // if so, set the interval entry to true
            for( Interval i : Interval.values() ) {
                long nextOccurence = ( prevTime / i.ms + 1 ) * i.ms;
                if( nextOccurence <= currTime )
                    this.intervals[ i.ordinal() ] = true;
            }

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
        GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
        Vector3fl fogColor = Environment.ENV.fogColor;
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
            Listener.GLOBAL_LISTENER.listen( new UpdateMessage( this.prevTime, this.currTime ) );
            this.prevTime = this.currTime;

            // then perform draws for all the various shader programs
            BlockShader.SHADER.use();
            Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderPrepareMessage() );
            Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderRenderMessage() );

            World.WORLD.refresh();
            this.updateCtx();
            this.prevTime = this.currTime;
        }

        // if we've exited the loop, the game is about to end. Try and clean up all resources by destroying all resources
        Listener.GLOBAL_LISTENER.listen( new DestroyMessage() );

        // finally destroy the display ctx
        this.destroyCtx();

    }
    
    public static void main( String[] args ) {
        Game g = new Game();
        g.run();
    }

}
