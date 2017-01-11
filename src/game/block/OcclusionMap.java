package game.block;

import game.Config;
import util.Vector2i;

public class OcclusionMap {

	private static int getDataLength() {
		return Config.CHUNK_DIM * Config.CHUNK_DIM;
	}
	
	private static int pack( Vector2i v ) {
		return v.x * Config.CHUNK_DIM + v.z;
	}
	
	private boolean[] occlusionMask;
	public boolean dirty;

	public OcclusionMap() {
		this.occlusionMask = new boolean[ getDataLength() ];
		this.dirty = false;
	}
	
	public void setOcclusion( Vector2i v, boolean b ) {
		int packed = pack( v );
		if( this.occlusionMask[ packed ] != b )
			this.dirty = true;
		this.occlusionMask[ packed ] = b;
	}
	
	public boolean getOcclusion( Vector2i v ) {
		return this.occlusionMask[ pack( v ) ];
	}
	
	public OcclusionMap fold( OcclusionMap other ) {
		for( int i = 0; i < getDataLength(); i += 1)
			this.occlusionMask[i] = this.occlusionMask[i] || other.occlusionMask[i];
		return this;
	}
	
	public OcclusionMap reset() {
		for( int i = 0; i < getDataLength(); i += 1)
			this.occlusionMask[i] = false;
		return this;
	}

}
