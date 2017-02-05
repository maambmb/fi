package game.input;

public enum KeyMap {
	
	WALK_FORWARD( Key.KEY_W ),
	WALK_BACKWARD( Key.KEY_S ),
	STRAFE_LEFT( Key.KEY_A ),
	STRAFE_RIGHT( Key.KEY_D );
	
	public Key mappedKey;
	private KeyMap( Key k ) {
		this.mappedKey = k;
	}
	
	
}
