package game.component;

import java.util.Iterator;
import game.Entity;
import game.InputArbiter;

public class InputListenerComponent implements Component {

	public InputArbiter.Priority priority;

	public InputListenerComponent( InputArbiter.Priority priority ) {
		this.priority = priority;
	}

	public Iterator<Integer> getPressedKeys() {
		return InputArbiter.GLOBAL.getPressedKeys();
	}
	
	public boolean isKeyDown( int keyCode ) {
		return InputArbiter.GLOBAL.isKeyDown( keyCode );
	}

	public boolean isPressed( int keyCode ) {
		return InputArbiter.GLOBAL.isPressed( keyCode );
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
