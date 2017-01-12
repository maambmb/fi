package game.block;

import java.util.HashMap;
import java.util.Map;

import game.Config;
import util.Lambda;
import util.Vector2i;
import util.Vector3i;

public class Chunk {

    // describes the state of the chunk in the context of requiring model recalculations
	public enum State {
        CLEAN,      // chunk's model hasn't changed
        DIRECT,     // chunk's model has changed
        INDIRECT,   // chunk's model has changed due to a neighbour chunk
        REQUIRED    // chunk is required to recalculate a changed chunk's model
	}
	
    // get the length of the array to store occlusion data
	private static int getOcclusionMapLength() {
		return Config.CHUNK_DIM * Config.CHUNK_DIM;
	}
	
	// convert a 3D int vector into the array index
	private static int pack( Vector2i v ) {
		return v.x + v.z * Config.CHUNK_DIM;
	}
	
	private Map<Vector3i,Block> blockMap; // the block positions
	private int[] occlusionMap;           // the heightmap for the chunk
	private int[] cumulativeOcclusionMap; // the heightmap for the chunk (incl. occlusions in the chunks directly above)
	public State state;
	
	public Chunk() {
		this.blockMap = new HashMap<Vector3i,Block>();
        int len =  getOcclusionMapLength();
		this.occlusionMap = new int[ len ];
		this.cumulativeOcclusionMap = new int[ len ];
		this.state = State.CLEAN;

        // initialize occlusion map to be one below the lowest possible block (represents no occlusion)
        for( int i = 0 ; i < len; i += 1 )
            this.occlusionMap[i] = Config.CHUNK_DIM;
	}
	
	public Block getBlock( Vector3i v ) {
		return this.blockMap.get( v );
	}
	
	public void setBlock( Vector3i v, Block b ) {
		
		Vector3i workingVector3i = new Vector3i().set( v );
		Block oldBlock = this.blockMap.get( v );

        // if old block is different from target block, the chunk has become dirty
        // and needs a recalc
		if( oldBlock != b) {
			this.state = State.DIRECT;
			if( b != null)
				this.blockMap.put( v,  b );
			else
                // if the block is null, remove the entry from the map
				this.blockMap.remove( v );
		}

        // get the index for the occlusion map
		int occlusionIx = pack( new Vector2i( v ) );
		if( b != null && b.blockType.opaque )
            // if the new block being added is opaque, update the heightmap with the new highest position
			this.occlusionMap[ occlusionIx ] = Math.min( v.y, this.occlusionMap[ occlusionIx ] );
		else if( oldBlock != null && oldBlock.blockType.opaque ) {
            // if we're moving from opaque -> not opaque, clear the heightmap entry
            // and iterate through all possible heights, trying to find the next highest entry
			this.occlusionMap[ occlusionIx ] = Config.CHUNK_DIM;
			for( int i = 0; i < Config.CHUNK_DIM; i += 1) {
				workingVector3i.y = i;
				Block nextOccluder = this.blockMap.get( workingVector3i );
                // if the found block is opaque then we can update the heightmap and stop
				if( nextOccluder != null && nextOccluder.blockType.opaque ) {
					this.occlusionMap[ occlusionIx ] = i;
					break;
				}
			}
		}

	}
	
	public void setupCumulativeOcclusionMap( Chunk ceilingChunk ) {
        // for each entry in the occlusion map, the new value
        // is equal to the corresponding occlusion map value,
        // unless the chunk above (ceiling) has an entry, in which case it is -1
        // representing an occlusion that comes from somewhere above the chunk
		for( int i = 0; i < getOcclusionMapLength(); i += 1) {
            int newVal = this.occlusionMap[i];
            if( ceilingChunk != null && ceilingChunk.cumulativeOcclusionMap[i] < Config.CHUNK_DIM )
                newVal = -1;
            // if there is a change in cum occlusion map value, then the chunk has become dirty
			if( newVal != this.cumulativeOcclusionMap[i] )
				this.state = State.DIRECT;
			this.cumulativeOcclusionMap[i] = newVal;
		}
	}
	
	public void iterateBlocks( Lambda.ActionTernary<Vector3i,Block,Boolean> fn ) {
        Vector2i workingVector2i = new Vector2i();
		for( Vector3i v : this.blockMap.keySet() )  {
            int packed = pack( workingVector2i.set( v ) );
			fn.run( v, this.blockMap.get( v ), v.y != this.cumulativeOcclusionMap[ packed ] );
        }
	}
	
}
