package game;

import game.message.UpdateMessage;

public class Game {

	public void run() {
		while( true ) {
			Listener.GLOBAL_LISTENER.listen( new UpdateMessage() );
			//do the shader draws
			
		}
	}
	
}
