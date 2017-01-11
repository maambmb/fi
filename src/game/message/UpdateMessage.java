package game.message;

public class UpdateMessage implements Message {

	public long deltaMs;
	
	public UpdateMessage( long deltaMs ) {
		this.deltaMs = deltaMs;
	}
	
}
