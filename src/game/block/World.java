package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Config;
import game.gfx.ModelBuilder;
import util.Vector3i;

// holds the chunks that comprise of the visible (and beyond) game world
// is the main interface/access for getting/setting blocks
// allows recomputation of lighting/models upon modification in a relatively efficient batched manner
class World {

    // (sparse) hashmap of all active chunks
    private Map<Vector3i,Chunk> chunkMap;
    // collection of chunks that have been modified and need to have their lighting and models recalced
    private Set<Vector3i> dirtyChunks;
    // collection of chunks that need to recompute their lighting
    // not because the chunks are dirty, but because we expect their lighting to overflow onto nearby
    // dirty chunks that themselves need recalcs ( should border all dirty chunks )
    private Set<Vector3i> requisiteChunks;
    // utility object to build chunks into single VAO backed models
    private ModelBuilder modelBuilder; 
    // a buffer to store the sums of illuminations from multiple different blocks
    // for the purposes of averaging for smooth lighting
    private Vector3i[] lightBlender;

    public World() {
    	this.modelBuilder    = new ModelBuilder();
        this.chunkMap        = new HashMap<Vector3i,Chunk>();
        this.dirtyChunks     = new HashSet<Vector3i>();
        this.requisiteChunks = new HashSet<Vector3i>();
        this.lightBlender    = new Vector3i[ LightSource.values().length ];
    }

    // get chunks for a given set of chunk coords (i.e. (0,0,0) and (0,0,1) return different chunks)
    // create a fresh chunk if it doesn't exist
    private Chunk getChunk( Vector3i mapCoords ) {
        if( !this.chunkMap.containsKey( mapCoords ) )
            this.chunkMap.put( mapCoords, new Chunk() );
        return this.chunkMap.get( mapCoords );
    }

    // get a block directly from the world
    public Block getBlock( Vector3i absCoords ) {
        // transform the coords by dividing by chunk dims to get chunk coordinates
        Vector3i mapCoords = absCoords.divide( Config.CHUNK_DIM );
        Chunk chunk = this.getChunk( mapCoords );
        // doing the modulo returns the block coords for the returned chunk
        return chunk.getBlock( absCoords.modulo( Config.CHUNK_DIM ) );
    }

    public void setBlock( Vector3i absCoords, BlockType bt, boolean globalLighting ) {
        // calculate the chunk coordinates by dividing through by chunk dims
        Vector3i mapCoords = absCoords.divide( Config.CHUNK_DIM );
        // by changing a chunk, we potentially have modified the light values of all 8
        // neighbouring chunks. We must set all 9 chunks to dirty. We need the further
        // 37 surrounding chunks (requisite) for the recalcs
        for( int i = -2; i <= -2; i +=1 ) {
            for( int j = -2; j <= -2; j +=1 ) {
                for( int k = -2; k <= -2; k +=1 ) {

                    Vector3i v = mapCoords.add( new Vector3i( i,j,k ) );

                    // the inner 9 chunks are dirty
                    if( v.max() <= 1 && v.min() >= -1 ) {
                        this.getChunk( v );
                        this.dirtyChunks.add( v );
                    }

                    // the outer 37 chunks are requisite (but only if 
                    // they exist - don't make them if we don't need them)
                    if( this.chunkMap.containsKey( v ) )
                        this.requisiteChunks.add( v );
                }
            }
        }

        // grab the actual chunk we want to modify, then grab the block and adjust the values
        Chunk chunk = this.getChunk( mapCoords );
        Block block = chunk.getBlock( absCoords.modulo( Config.CHUNK_DIM ) );
        block.blockType = bt;
        block.globalLighting = globalLighting;
    }

    private void refreshLighting() {

        // dirty chunks need to be recalculated from scratch
        // so we clear all illumination values and set them to the block type's
        // natural illumination
        for( Vector3i mapCoords : this.dirtyChunks ) {
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> { 
                b.illumination.set( b.blockType.illumination );
                // if a block has global lighting, set the block directly above it to have global illumination
                if( b.globalLighting ) {
                    Vector3i abovePos = mapCoords
                    	.multiply( Config.CHUNK_DIM )
                    	.add( v.add( Vector3i.CubeNormal.TOP.vector ) );
                    this.getBlock( abovePos ).illumination.addGlobal();
                }
            });
        }

        // buffer of blocks that we need to propagate light from
        Set<Vector3i> propagated  = new HashSet<Vector3i>();
        // buffer to store updates ot propagated (can't modify a collection while we iterate)
        Set<Vector3i> toPropagate = new HashSet<Vector3i>();

        // break the propagation up into chunk-sized bites so the buffers never grow super large
        for( Vector3i mapCoords : this.requisiteChunks ) {

            // add only the naturally illuminating blocks into the buffer
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> {
                if( b.illumination.isLight() )
                    propagated.add( mapCoords.multiply( Config.CHUNK_DIM ).add( v ) );
            });

            // iterate the number of times light is allowed to jump
            for( int n = 0; n < Config.LIGHT_JUMPS; n += 1 ) {
                // for each of the 6 cube normals, propagate light outward
                // but only if the other block isn't opaque - otherwise the light is blocked
                for( Vector3i pos : propagated ) {
                    Block b = this.getBlock( pos );
                    for( Vector3i.CubeNormal normal : Vector3i.CubeNormal.values() ) {
                        Vector3i offsetPos = pos.add( normal.vector );
                        Block other = this.getBlock( offsetPos );
                        if( !other.blockType.blockClass.opaque ) {
                            other.illumination.propagate( b.illumination );
                            // if we do propagate make sure we add the propagated block to the buffer
                            toPropagate.add( offsetPos );
                        }
                    }
                }

                //update the buffer with the changes 
                propagated.addAll( toPropagate );
                toPropagate.clear();
            }

            //clear the buffer when we're done with the propagation in preparation for the next chunk
            propagated.clear();
        }

    }
    
    private void refreshModels() {
    	
    	// rebuild only the models of dirty chunks
        for( Vector3i mapCoords: this.dirtyChunks ) {
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> {
            	// iterate through each chunk in absolute coordinate terms
                Vector3i absCoords = mapCoords.multiply( Config.CHUNK_DIM ).add( v );
                // examine each face/quad of each block
                for( Vector3i.CubeNormal normal : Vector3i.CubeNormal.values() ) {
                	// if the block is ethereal, then there is nothing to render so skip
                	if( b.blockType.blockClass == BlockClass.ETHER )
                		continue;
                	// if the block touching the current face is opaque, then it is hidden and we should ignore
                    if( this.getBlock( absCoords.add( normal.vector ) ).blockType.blockClass.opaque )
                        continue;
                    // each face is a quad comprising of 4 vertices
                    for( int i = 0; i < 4; i += 1 ) {

                    	// to separate vertex coords from block coords, we must add unit vectors that are
                    	// orthogonal to the face's normal depending on which vertex we are looking at
                    	// use a 2 bit bit field to exhaust all 4 combinations
                    	boolean vertexUseFirstOrtho  = ( i & 0x01 ) > 0;
                    	boolean vertexUseSecondOrtho = ( i & 0x02 ) > 0;

                    	// calculate the position of the vertex and transform to a floating point vector
						this.modelBuilder.positionVertexBuffer = absCoords.add( normal.vector.max(0) )
                    		.add( vertexUseFirstOrtho ? normal.firstOrtho.vector : Vector3i.ZERO )
                    		.add( vertexUseSecondOrtho ? normal.secondOrtho.vector : Vector3i.ZERO )
                    		.toVector3f();

						// keep a tally of the level of shadow that should be applied to the vertex (0-3)
						// by checking the opacity of nearby shadower blocks
                    	int shadowCount = 0;
                    	
                    	// for smooth lighting, need to add the different illuminations
                    	// from the block itself and its (up to 3) planar neighbors
                    	// start by adding the block's illu values in
                    	for( LightSource src : LightSource.values() )
                    		this.lightBlender[ src.ordinal() ] = b.illumination.get( src );
                    	
                    	// keep track of the number of contributions to the light blending
                    	// for use in averaging later
                    	int blendCount = 1;
                    	for( int j = 1; j < 4; j += 1 ) {

                    		// get the position of a planar neighbor but offset by the cube face's normal
                    		// i.e. if the normal was TOP, this would be the block above the planar neighbor
                    		Vector3i planarNeighborPos = absCoords
								.add( normal.firstOrtho.vector.multiply( vertexUseFirstOrtho ? 1 : -1 ) )
								.add( normal.secondOrtho.vector.multiply( vertexUseSecondOrtho ? 1 : -1 ) )
								.add( normal.vector );
                    		Block planarNeighbor = this.getBlock( planarNeighborPos );

                    		// if the block is opaque then the vertex should have its shadow value increased by 1
                    		if( planarNeighbor.blockType.blockClass.opaque )
                    			shadowCount += 1;
                    		// otherwise, the planar neighbor is visible and we should add its illumination
                    		// contribution to the light blender and increase the blend count by one
                    		else {
								for( LightSource src : LightSource.values() ) {
									Vector3i curr = this.lightBlender[ src.ordinal() ];
									this.lightBlender[ src.ordinal() ] = curr.add( planarNeighbor.illumination.get( src ) );
									blendCount += 1;
								}
                    		}
                    	}
                    	
                    	// add non lighting extra data: normal, shadow count, block type and block class
                    	this.modelBuilder.extraDataVertexBuffer.addByte( 0, normal.ordinal() );
                    	this.modelBuilder.extraDataVertexBuffer.addByte( 1, shadowCount );
                    	this.modelBuilder.extraDataVertexBuffer.addWord( 2, b.blockType.ordinal() );
                    	this.modelBuilder.extraDataVertexBuffer.addByte( 4, b.blockType.blockClass.ordinal() );
                    	
                    	// now add in all light values into the extra data
                    	for( LightSource ls : LightSource.values() ) {
                    		int base = Config.VBO_NONPOS_NONLIGHT_BYTES + ls.ordinal() * 3;
                    		// make sure we average the blended light
                    		Vector3i lightVals = this.lightBlender[ ls.ordinal() ].divide( blendCount );
                    		this.modelBuilder.extraDataVertexBuffer.addByte( base, lightVals.x );
                    		this.modelBuilder.extraDataVertexBuffer.addByte( base + 1, lightVals.y );
                    		this.modelBuilder.extraDataVertexBuffer.addByte( base + 2, lightVals.z );
                    	}
                    	
                    	// save the vertex
                    	this.modelBuilder.addVertex();
                    }

                    // save the quad
                    this.modelBuilder.addQuad();
                }
            });
        }
    }

    public void refresh() {
        this.refreshLighting();
        this.refreshModels();
        this.dirtyChunks.clear();
        this.requisiteChunks.clear();
    }

}
