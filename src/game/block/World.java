package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import game.Config;
import util.Vector2i;
import util.Vector3i;

public class World {
	
	private Map<Zone,Set<Vector3i>> zoneMap;
	private Map<Vector3i,Chunk> chunkMap;
	private Map<Vector2i,Set<Vector3i>> chunkColumns;

	public World() {

		this.chunkMap = new HashMap<Vector3i,Chunk>();
		this.zoneMap = new HashMap<Zone,Set<Vector3i>>();
		this.chunkColumns = new HashMap<Vector2i,Set<Vector3i>>();
		
		for( Zone z : Zone.ZONES )
			this.zoneMap.put( z, new HashSet<Vector3i>() );
	}
	
	// ensure a chunk is available prior to writing to it
	private void tryCreateChunk( Vector3i chunkVec ) {
		if( ! this.chunkMap.containsKey( chunkVec ) ) {
			// ensure a chunk column is available
			Vector2i colVec = new Vector2i( chunkVec.x, chunkVec.z );
			if( ! this.chunkColumns.containsKey( colVec ) )
				this.chunkColumns.put( colVec, new TreeSet<Vector3i>( (l,r) -> l.y - r.y ) );
			this.chunkColumns.get( colVec ).add( chunkVec );
			this.chunkMap.put( chunkVec, new Chunk() );
		}
	}
	
	public void addBlock( Vector3i v, Zone z, Block b) {
		// split the vector into chunk and block coordinates
		Vector3i chunkVec =  v.clone().divide( Config.CHUNK_DIM );
		Vector3i blockVec = v.clone().modulo( Config.CHUNK_DIM );
		this.tryCreateChunk( chunkVec );
		
		// assign the chunk to the zone ( 1 -> many mapping)
		this.zoneMap.get( z ).add( chunkVec );
		Chunk c = this.chunkMap.get( chunkVec );
		c.addBlock( blockVec, b);

	}

	public void removeBlock( Vector3i v ) {
		Vector3i chunkVec =  v.clone().divide( Config.CHUNK_DIM );
		Vector3i blockVec = v.clone().modulo( Config.CHUNK_DIM );

		// only bother proceeding if the chunk exists
		if( this.chunkMap.containsKey( chunkVec ) ) {
			Chunk c = this.chunkMap.get( chunkVec );
			c.clearBlock( blockVec );
		}
	}	

	// re-calculate the global light available at the top of each chunk 
	public void regenerateCumulativeOcclusionMaps( Vector2i col ) {
		Chunk prevChunk = null;
		// loop through each chunk in the column (from highest -> lowest )
		for( Vector3i chunkVec : this.chunkColumns.get( col ) ) {
			Chunk c = this.chunkMap.get( chunkVec );
			// first blank the cumulative occlusion map, then fold in the previous chunk's cumulative and non-cumulative occlusion maps
			c.cumulativeOcclusionMap.reset();
			if( prevChunk != null )
				c.cumulativeOcclusionMap.fold( prevChunk.occlusionMap ).fold( prevChunk.cumulativeOcclusionMap );
			prevChunk = c;
		}
	}
	
	public Chunk getChunk( Vector3i v) {
		return this.chunkMap.get(v);
	}
	
}
