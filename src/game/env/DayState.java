package game.env;

import util.Vector3fl;
import util.Vector3in;

public enum DayState {
	DAWN( 2000, new Vector3in( 0x091b36 ), Vector3in.WHITE ),
	SUNRISE( 6000, new Vector3in( 0xe0f8ff ), Vector3in.ZERO ),
	MIDDAY( 2000, new Vector3in( 0xe0f8ff ), Vector3in.ZERO ),
	SUNSET( 6000, new Vector3in( 0xf5bfda ), Vector3in.ZERO ),
	DUSK( 6000, new Vector3in( 0x05142b ), Vector3in.WHITE ),
	NIGHT( 6000, new Vector3in( 0x05142b ), Vector3in.WHITE );
	
	public long durationMs;
	public Vector3in globalLight;
	public Vector3in nightLight;

	private DayState( long durationMs, Vector3in globalLight, Vector3in nightLight ) {
		this.durationMs = durationMs;
		this.globalLight = globalLight;
		this.nightLight = nightLight;
	}
	
	public DayState nextDayState() {
		int ix = ( this.ordinal() + 1 ) % DayState.values().length;
		return DayState.values()[ ix ];
	}
	
	public DayState prevDayState() {
		int ix = ( this.ordinal() + DayState.values().length - 1 ) % DayState.values().length;
		return DayState.values()[ ix ];
	}

	public Vector3in getNightLight( float progress ) {
		DayState prev = this.prevDayState();
    	Vector3fl startLight = prev.nightLight.toVector3fl().multiply( 1f - progress );
    	Vector3fl endLight = this.nightLight.toVector3fl().multiply( progress );
    	return startLight.add( endLight ).toVector3in();
	}
	
	public Vector3in getGlobalLight( float progress ) {
		DayState prev = this.prevDayState();
    	Vector3fl startLight = prev.globalLight.toVector3fl().multiply( 1f - progress );
    	Vector3fl endLight = this.globalLight.toVector3fl().multiply( progress );
    	return startLight.add( endLight ).toVector3in();
	}
	
}
