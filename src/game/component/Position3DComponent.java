package game.component;

import game.Entity;
import game.Listener;
import game.message.UpdateMessage;

public final class Position3DComponent implements Component {
	
	private int updateIx;
	
	public Position3DComponent() { 
	}
	
	public void update( UpdateMessage msg ) {
		// do something to this
	}

	public void destroy() {
		Listener.GLOBAL_LISTENER.removeListener( this.updateIx );
	}

	public void setup( Entity e ) {
		this.updateIx = Listener.GLOBAL_LISTENER.addListener( UpdateMessage.class, this::update );
	}

}
