package game;

import game.message.UpdateMessage;

public class Game {

	public void run() {
		while( true ) {
			Listener.GLOBAL_LISTENER.listen( new UpdateMessage( 0 ) );
			//do the shader draws
			
		}
	}
	
}
