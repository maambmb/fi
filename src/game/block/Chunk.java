package game.block;

import game.Config;
import util.Lambda;
import util.Vector2i;
import util.Vector3i;

public class Chunk {

	//calculate the length of the block data array
	private static int getDataLength() {
		return Config.CHUNK_DIM * Config.CHUNK_DIM * Config.CHUNK_DIM;
	}
	
	// convert a 3D int vector into the array index
	private static int pack( Vector3i v ) {
		int total = v.x;
		total += v.z * Config.CHUNK_DIM;
		total += v.y * Config.CHUNK_DIM * Config.CHUNK_DIM;
		return total;
	}

	// convert an array index into a 3D int vector
	private static Vector3i unpack( Vector3i v, int packed ) {
		int dimSq = Config.CHUNK_DIM * Config.CHUNK_DIM;
		v.y = packed / dimSq;
		int rem = packed % dimSq;
		v.z = rem / Config.CHUNK_DIM;
		v.x = rem % Config.CHUNK_DIM;
		return v;
	}
	
	public OcclusionMap occlusionMap;
	public OcclusionMap cumulativeOcclusionMap;
	private Block[] blockData;
	public boolean dirty;
	
	public Chunk() {
		this.blockData = new Block[ getDataLength() ];
		this.occlusionMap = new OcclusionMap();
		this.dirty = false;
	}
	
	public Block getBlock( Vector3i v ) {
		return this.blockData[ pack( v ) ];
	}
	
	private void setBlock( Vector3i v, Block b ) {
		int packed = pack( v );
		// if the block type has changed, mark the chunk as dirty
		if( this.blockData[ packed ] != b )
			this.dirty = true;
		this.blockData[ packed ] = b;
	}
	
	public void addBlock( Vector3i v, Block b) {
		this.setBlock( v , b);
		// if the added block is opaque, set 2D cell as occluded
		if( b.blockType == Block.Type.OpaqueBlock )
			this.occlusionMap.setOcclusion( new Vector2i( v.x, v.z ), true );
	}
	
	public void clearBlock( Vector3i v ) {
		Block b = this.getBlock( v );
		this.setBlock( v, null );

		// recalculate occlusion if the removed block was solid
		if( b != null && b.blockType == Block.Type.OpaqueBlock ) {
			Vector2i v2 = new Vector2i( v.x, v.z );
			Vector3i workingVec = new Vector3i().set( v );
			// remove the occlusion entirely
			this.occlusionMap.setOcclusion( v2, false );
			// then loop through each block of the column and re-add the occlusion if necessary
			for( int i = 0; i < Config.CHUNK_DIM; i += 1) {
				workingVec.y = i;
				Block nextB = this.getBlock( workingVec );
				if( nextB != null && nextB.blockType == Block.Type.OpaqueBlock ) {
					this.occlusionMap.setOcclusion( v2, true );
					return;
				}
			}
		}
	}

	public void iterateBlocks( Lambda.ActionBinary<Chunk,Vector3i> fn ) {
		Vector3i vec = new Vector3i();
		for( int i = 0; i < getDataLength(); i += 1 )
			if( this.blockData[ i ] != null )
				fn.run( this, unpack( vec, i) );
	}
	
	public boolean isDirty() {
		return this.cumulativeOcclusionMap.dirty || this.dirty;
	}
	
	public void clean() {
		this.dirty = false;
		this.cumulativeOcclusionMap.dirty = false;
	}
	
}
