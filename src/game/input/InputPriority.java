package game.input;

public enum InputPriority {

	TOP_LISTENER( false ),
	GUI_01( true ),
	CONTROL( true );
	
	private InputPriority( boolean capturing ) {
		this.capturing = capturing;
	}
	
	public boolean capturing;

}
