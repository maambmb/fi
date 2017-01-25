package game;

import game.block.World;
import game.gfx.shader.BlockShader;
import game.listener.Listener;

import util.Vector3in;

public class Game {

    public class UpdateMessage {

        public long deltaMs;
        public UpdateMessage( long deltaMs ) {
            this.deltaMs = deltaMs;
        }
        
    }

	public void run() {
        
        Vector3in.CubeNormal.init();
        Listener.init();
        BlockShader.init();
        World.init();

        Camera.init();
        Environment.init();

		while( true ) {
			Listener.GLOBAL_LISTENER.listen( new UpdateMessage( 0 ) );
			//do the shader draws
			Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderPrepareMessage() );
			Listener.GLOBAL_LISTENER.listen( new BlockShader.BlockShaderRenderMessage() );
		}
	}
	
}
