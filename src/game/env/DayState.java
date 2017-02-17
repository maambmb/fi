package game.env;

import util.Vector3in;

public enum DayState {

	DAWN( 1800, new Vector3in( 0x507896) ),
	SUNRISE( 1800, new Vector3in( 0xfdf7ea ) ),
	MIDDAY_START( 1800, new Vector3in(0xeafdfd) ),
	MIDDAY( 3600, new Vector3in( 0xeafdfd ) ),
	SUNSET( 1800, new Vector3in( 0xfdf7ea ) ),
	DUSK( 1800, new Vector3in( 0x507896 ) ),
	NIGHT_START( 1800, new Vector3in( 0x363744 ) ),
	NIGHT( 3600, new Vector3in( 0x363744 ) );

	
	public int durationMs;
	public Vector3in skyColor;

	private DayState( int durationMs, Vector3in skyColor ) {
		this.durationMs = durationMs;
		this.skyColor = skyColor;
	}
	
	public DayState nextDayState() {
		int ix = ( this.ordinal() + 1 ) % DayState.values().length;
		return DayState.values()[ ix ];
	}
	
}
