package game;

public class GlobalSubscriberComponent implements Component {

	private Listener listener;

	public GlobalSubscriberComponent( Entity e ) {
		this.listener = e.listener;
	}
	
	public void init() {
		Listener.GLOBAL.addChild( this.listener );
	}
	
	public void destroy() {
		Listener.GLOBAL.removeChild( this.listener );
	}
	
}
