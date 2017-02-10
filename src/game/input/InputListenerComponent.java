package game.input;

import game.Component;
import game.Entity;

public class InputListenerComponent implements Component {

	public InputArbiter.Priority priority;

	public InputListenerComponent( InputArbiter.Priority priority ) {
		this.priority = priority;
	}

	public Iterable<Key> getPressedKeys() {
		return InputArbiter.GLOBAL.getPressedKeys();
	}
	
	public boolean isKeyDown( Key key ) {
		return InputArbiter.GLOBAL.isKeyDown( key );
	}
	
	public boolean isKeyDown( KeyMap map ) {
		return this.isKeyDown( map.mappedKey );
	}

	public boolean isPressed( Key key ) {
		return InputArbiter.GLOBAL.isPressed( key );
	}

	public boolean isPressed( KeyMap map ) {
		return this.isPressed( map.mappedKey );
	}
	
	public boolean canListen() {
		return InputArbiter.GLOBAL.canListen( this );
	}
	
	public void startListening() {
		InputArbiter.GLOBAL.addListener( this );
	}
	
	public void stopListening() {
		InputArbiter.GLOBAL.removeListener( this );
	}

	@Override
	public void setup(Entity e) {
	}

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		this.stopListening();
	}
	
	
	
}
