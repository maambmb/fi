package game.env;

import util.Vector3in;

public enum DayState {
	DAWN( 6000, new Vector3in( 0x011427 ), new Vector3in( 0xac758a ) ),
	SUNRISE( 2000, new Vector3in( 0xac758a ), new Vector3in( 0xa5d7ea ) ),
	DAY( 6000, new Vector3in(0xa5d7ea ), new Vector3in( 0xa5d7ea ) ),
	SUNSET( 2000, new Vector3in( 0xa5d7ea), new Vector3in( 0xac758a ) ),
	DUSK( 6000, new Vector3in(0xac758a ), new Vector3in( 0x011427 ) ),
	NIGHT( 6000, new Vector3in( 0x011427 ), new Vector3in( 0x011427 ) );
	
	public long durationMs;
	public Vector3in startLight;
	public Vector3in endLight;
	private DayState( long durationMs, Vector3in startLight, Vector3in endLight ) {
		this.durationMs = durationMs;
		this.startLight = startLight;
		this.endLight = endLight;
	}
	
}
