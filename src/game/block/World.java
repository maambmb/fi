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
    
    private Map<Zone,Set<Vector3i>> zoneMap;            // maps a zone to all its constituent chunks (sets not mutually exclusive)
    private Map<Vector3i,Chunk> chunkMap;               // maps location to chunk
    private Map<Vector2i,Set<Vector3i>> chunkColumns;   // maps 2d location to columns of stacked chunks
    
    private Set<Vector2i> dirtyColumns;  // a temporary store of columns that have dirty chunks

    public World() {

        this.chunkMap = new HashMap<Vector3i,Chunk>();
        this.zoneMap = new HashMap<Zone,Set<Vector3i>>();
        this.chunkColumns = new HashMap<Vector2i,Set<Vector3i>>();
        
        // initialize the zone map with all zones
        for( Zone z : Zone.ZONES )
            this.zoneMap.put( z, new HashSet<Vector3i>() );
    }
    
    public void setBlock( Vector3i v, Zone z, Block b) {
        // find out the coords for the chunk, the underlying chunk's blocks
        Vector3i chunkVec =  v.clone().divide( Config.CHUNK_DIM );
        Vector3i blockVec = v.clone().modulo( Config.CHUNK_DIM );
        
        // if the chunk doesn't exist, we must create it
        if( !this.chunkColumns.containsKey( chunkVec ) ) {
            // unless we're not actually adding a block - just quit out at this point
            if( b == null)
                return;
            // get the column coordinates, and ensure a column exists
            Vector2i colVec = new Vector2i().set( chunkVec );
            if( ! this.chunkColumns.containsKey( colVec ) )
                // ensure the column set sorts based on height (y value) such that highest chunks come first
                // this makes cumulative occlusion map calcs way easier
                this.chunkColumns.put( colVec, new TreeSet<Vector3i>( (l,r) -> l.y - r.y ) );
            this.chunkColumns.get( colVec ).add( chunkVec );
            this.chunkMap.put( chunkVec, new Chunk() );
        }
        
        // assign the chunk to the zone ( 1 -> many mapping)
        this.zoneMap.get( z ).add( chunkVec );
        Chunk c = this.chunkMap.get( chunkVec );
        c.setBlock( blockVec, b);
    }
    
    public void propagateChunkStates() {

        // find all columns that have chunks that have been modified - these columns will need to have their
        // cum occlusion maps recalculated...
        for( Vector3i chunkVec : this.chunkMap.keySet() ) {
            if( this.chunkMap.get( chunkVec ).state == Chunk.State.DIRECT ) {
                this.dirtyColumns.add( new Vector2i().set( chunkVec ) );
            }
        }
        
        // recalculate the cumulative occlusion maps
        // each chunk will know what areas are occluded (even if the occlusion happens some n chunks above it)
        for( Vector2i colVec : this.dirtyColumns ) {
            Chunk prevChunk = null;
            // loop through each chunk, starting at the highest, updating occlusion maps as you go
            for( Vector3i chunkVec : this.chunkColumns.get( colVec ) ) {
                Chunk c = this.chunkMap.get( chunkVec );
                c.setupCumulativeOcclusionMap( prevChunk );
                prevChunk = c;
            }
        }

        // clear the dirty columns set, we don't need this info anymore
        this.dirtyColumns.clear();

        // find neighbours of directly modified chunks and set them as indirectly dirty (light changes from
        // neighbours might affect the chunk's model)
        for( Vector3i chunkVec : this.chunkMap.keySet() ) {
            if( this.chunkMap.get( chunkVec ).state == Chunk.State.DIRECT ) {
                for( Vector3i.Normal normal : Vector3i.Normal.NORMALS ) {
                    Vector3i offset = chunkVec.clone().add( normal.vector );
                    Chunk c = this.chunkMap.get( offset );
                    if( c != null && c.state == Chunk.State.CLEAN )
                        c.state = Chunk.State.INDIRECT;
                }
            }
        }

        // find neighbours of indirectly dirty chunks and mark them as required, in that they
        // will be required in the model recalculations for the indirectly dirty chunks
        for( Vector3i chunkVec : this.chunkMap.keySet() ) {
            if( this.chunkMap.get( chunkVec ).state == Chunk.State.INDIRECT ) {
                for( Vector3i.Normal normal : Vector3i.Normal.NORMALS ) {
                    Vector3i offset = chunkVec.clone().add( normal.vector );
                    Chunk c = this.chunkMap.get( offset );
                    if( c != null && c.state == Chunk.State.CLEAN )
                        c.state = Chunk.State.REQUIRED;
                }
            }
        }
    }
    
    public Chunk getChunk( Vector3i v) {
        return this.chunkMap.get(v);
    }
    
}
