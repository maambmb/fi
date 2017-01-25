package game;

import game.listener.Listener;

public class Game {

    public class UpdateMessage {

        public long deltaMs;
        public UpdateMessage( long deltaMs ) {
            this.deltaMs = deltaMs;
        }
        
    }

	public void run() {
		while( true ) {
			Listener.GLOBAL_LISTENER.listen( new UpdateMessage( 0 ) );
			//do the shader draws
		}
	}
	
}
