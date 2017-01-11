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
	
	private Set<Vector2i> dirtyColumns;

	public World() {

		this.chunkMap = new HashMap<Vector3i,Chunk>();
		this.zoneMap = new HashMap<Zone,Set<Vector3i>>();
		this.chunkColumns = new HashMap<Vector2i,Set<Vector3i>>();
		
		for( Zone z : Zone.ZONES )
			this.zoneMap.put( z, new HashSet<Vector3i>() );
	}
	
	public void setBlock( Vector3i v, Zone z, Block b) {
		Vector3i chunkVec =  v.clone().divide( Config.CHUNK_DIM );
		Vector3i blockVec = v.clone().modulo( Config.CHUNK_DIM );
		Vector2i colVec = new Vector2i().set( chunkVec );
		
		if( !this.chunkColumns.containsKey( chunkVec ) ) {
			if( b == null)
				return;
			if( ! this.chunkColumns.containsKey( colVec ) )
				this.chunkColumns.put( colVec, new TreeSet<Vector3i>( (l,r) -> l.y - r.y ) );
			this.chunkColumns.get( colVec ).add( chunkVec );
			this.chunkMap.put( chunkVec, new Chunk() );
		}
		
		// assign the chunk to the zone ( 1 -> many mapping)
		this.zoneMap.get( z ).add( chunkVec );
		Chunk c = this.chunkMap.get( chunkVec );
		c.setBlock( blockVec, b);
	}

	// re-calculate the global light available at the top of each chunk 
	public void regenerateCumulativeOcclusionMaps( Vector2i col ) {
		Chunk prevChunk = null;
		// loop through each chunk in the column ( y = 0, y = 1, etc...)
		for( Vector3i chunkVec : this.chunkColumns.get( col ) ) {
			Chunk c = this.chunkMap.get( chunkVec );
			c.setupCumulativeOcclusionMap( prevChunk );
			prevChunk = c;
		}
	} 
	
	public void propagateChunkStates() {

		// find all columns that have been modified
		for( Vector3i chunkVec : this.chunkMap.keySet() ) {
			if( this.chunkMap.get( chunkVec ).state == Chunk.State.DIRTY ) {
				this.dirtyColumns.add( new Vector2i().set( chunkVec ) );
			}
		}
		
		// regenerate the cum-occlusion maps for all chunks in the modified columns
		for( Vector2i colVec : this.dirtyColumns )
			this.regenerateCumulativeOcclusionMaps( colVec );

		this.dirtyColumns.clear();

		// now scan through the entire chunk map, setting neighbours of dirty chunks as "indirectly dirty"
		for( Vector3i chunkVec : this.chunkMap.keySet() ) {
			if( this.chunkMap.get( chunkVec ).state == Chunk.State.DIRTY ) {
				for( Vector3i.Normal normal : Vector3i.Normal.NORMALS ) {
					Vector3i offset = chunkVec.clone().add( normal.vector );
					Chunk c = this.chunkMap.get( offset );
					if( c != null && c.state == Chunk.State.CLEAN )
						c.state = Chunk.State.INDIRECTLY_DIRTY;
				}
			}
		}
	}
	
	public Chunk getChunk( Vector3i v) {
		return this.chunkMap.get(v);
	}
	
}
