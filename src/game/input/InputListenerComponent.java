package game.input;

import game.Component;

public class InputListenerComponent implements Component {

	public InputPriority priority;

	public InputListenerComponent( InputPriority priority ) {
		this.priority = priority;
	}

	public boolean canListen() {
		return InputCapturer.GLOBAL.canListen( this );
	}
	
	public void startListening() {
		InputCapturer.GLOBAL.addListener( this );
	}
	
	public void stopListening() {
		InputCapturer.GLOBAL.removeListener( this );
	}

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		this.stopListening();
	}
	
	
	
}
