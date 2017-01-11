package game.block;

import java.util.HashMap;
import java.util.Map;

import game.Config;
import util.Lambda;
import util.Vector2i;
import util.Vector3i;

public class Chunk {

	public enum State {
		CLEAN, DIRTY, INDIRECTLY_DIRTY
	}
	
	private static int getOcclusionMapLength() {
		return Config.CHUNK_DIM * Config.CHUNK_DIM;
	}
	
	// convert a 3D int vector into the array index
	private static int pack( Vector2i v ) {
		return v.x + v.z * Config.CHUNK_DIM;
	}
	
	private Map<Vector3i,Block> blockMap;
	private boolean[] occlusionMap;
	private boolean[] cumulativeOcclusionMap;
	public State state;
	
	public Chunk() {
		this.blockMap = new HashMap<Vector3i,Block>();
		this.occlusionMap = new boolean[ getOcclusionMapLength() ];
		this.cumulativeOcclusionMap = new boolean[ getOcclusionMapLength() ];
		this.state = State.CLEAN;
	}
	
	public Block getBlock( Vector3i v ) {
		return this.blockMap.get( v );
	}
	
	public void setBlock( Vector3i v, Block b ) {
		
		Vector2i workingVector2i = new Vector2i().set( v );
		Vector3i workingVector3i = new Vector3i().set( v );

		int occlusionIx = pack( workingVector2i );
		Block oldBlock = this.blockMap.get( v );
		if( oldBlock != b) {
			this.state = State.DIRTY;
			if( b != null)
				this.blockMap.put( v,  b );
			else
				this.blockMap.remove( v );
		}
		if( b != null && b.blockType == Block.Type.OpaqueBlock )
			this.occlusionMap[ occlusionIx ] = true;
		else if( oldBlock != null && oldBlock.blockType == Block.Type.OpaqueBlock ) {
			this.occlusionMap[ occlusionIx ] = false;
			for( int i = 0; i < Config.CHUNK_DIM; i += 1) {
				workingVector3i.y = i;
				Block nextOccluder = this.blockMap.get( workingVector3i );
				if( nextOccluder != null && nextOccluder.blockType == Block.Type.OpaqueBlock ) {
					this.occlusionMap[ occlusionIx ] = true;
					break;
				}
			}
		}

	}
	
	public void setupCumulativeOcclusionMap( Chunk ceilingChunk ) {
		for( int i = 0; i < getOcclusionMapLength(); i += 1) {
			boolean newVal = this.occlusionMap[i] || ceilingChunk == null ? false : ceilingChunk.cumulativeOcclusionMap[i];
			if( newVal != this.cumulativeOcclusionMap[i] )
				this.state = State.DIRTY;
			this.cumulativeOcclusionMap[i] = newVal;
		}
	}
	
	public void iterateBlocks( Lambda.ActionBinary<Chunk,Vector3i> fn ) {
		for( Vector3i v : this.blockMap.keySet() ) 
			fn.run( this, v );
	}
	
}
