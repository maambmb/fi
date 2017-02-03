package game.component;

import game.Entity;
import game.Listener;

public class GlobalSubscriberComponent implements Component {

	private Listener listener;

	public GlobalSubscriberComponent() {
		
	}
	
	public void setup( Entity e ) {
		this.listener = e.listener;
	}
	
	public void init() {
		Listener.GLOBAL_LISTENER.addChild( this.listener );
	}
	
	public void destroy() {
		Listener.GLOBAL_LISTENER.removeChild( this.listener );
	}
	
}
