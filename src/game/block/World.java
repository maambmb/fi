package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import game.Config;
import game.block.Block.Opacity;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.TextureRef;
import util.Matrix4fl;
import util.Vector3fl;
import util.Vector3in;

public class World {

    // holds the chunks that comprise of the visible (and beyond) game world
    // is the main interface/access for getting/setting blocks
    // allows recomputation of lighting/models upon modification in a relatively efficient batched manner

	private static Matrix4fl matrix = new Matrix4fl();

    public static World WORLD;
    public static void init() {
        WORLD = new World();
    }
    
    // comparator that orders higher vectors before lower vectors (useful for occlusion calcs)
    private static int columnCompare( Vector3in l, Vector3in r ) {
    	return r.y - l.y;
    }

    // places the coordinates into chunk coordinate buckets
    // similar to divide but handles negative cases correctly
    // TODO: is there a more efficient way to do this?
    private static Vector3in bucket( Vector3in v ) {
    	Vector3in mod = v.modulo( Config.CHUNK_DIM );
    	Vector3in div = v.divide( Config.CHUNK_DIM );
    	if( v.x < 0 && mod.x != 0 )
    		div.x -= 1;
    	if( v.y < 0 && mod.y != 0 )
    		div.y -= 1;
    	if( v.z < 0 && mod.z != 0 )
    		div.z -= 1;
    	return div;
    }   

    // (sparse) hashmap of all active chunks
    private Map<Vector3in,Chunk> chunkMap;
    
    // (sparse) hashmap of chunk columns
    private Map<Vector3in,TreeSet<Vector3in>> chunkColumns;
    
    public World() {
        this.chunkMap      = new HashMap<Vector3in,Chunk>();
        this.chunkColumns = new HashMap<Vector3in,TreeSet<Vector3in>>();
    }

    private Chunk getChunk( Vector3in chunkCoords ) {

    	// create a new chunk if it doesn't exist
        if( !this.chunkMap.containsKey( chunkCoords ) ) {
            this.chunkMap.put( chunkCoords, new Chunk() );

            // take the column vector of the chunk (i.e. set the height cmpt to 0 )
        	Vector3in col = new Vector3in( chunkCoords.x, 0, chunkCoords.z );
        	
        	// if a column doesn't exist, create it
        	if( !this.chunkColumns.containsKey( col ) )
        		// the set that holds the column vectors must be sorted by height (highest first)
        		this.chunkColumns.put( col, new TreeSet<Vector3in>( World::columnCompare ) );

        	// add the chunk coords to the column
        	this.chunkColumns.get( col ).add( chunkCoords );
        }
        return this.chunkMap.get( chunkCoords );
    }
    
    public BlockContext getBlockContext( Vector3in coords ) {
    	Vector3in chunkCoords = bucket( coords );
    	Vector3in blockCoords = chunkCoords.multiply( - Config.CHUNK_DIM ).add( coords );
        Chunk chunk = this.getChunk( chunkCoords );
        return chunk.getBlockContext( blockCoords );
    }

    public void setBlock( Vector3in absCoords, Block bt ) {
    	Vector3in chunkcoords = bucket( absCoords );
    	Vector3in blockCoords = chunkcoords.multiply( - Config.CHUNK_DIM ).add( absCoords );
        Chunk chunk = this.getChunk( chunkcoords );
        BlockContext ctx = chunk.getBlockContext( blockCoords );
        
        // if the block is changing to something new from its current value, flag the chunk as
        // having been directly modified via a block change
        if( ctx.block != bt )
        	chunk.state |= ChunkState.DIRTY_BLOCK.flag;

        ctx.block = bt;
    }
    
    private void propagateOcclusion() {

    	// for each column of chunks
    	for( TreeSet<Vector3in> set : this.chunkColumns.values() ) {
    		
    		// loop through all chunks of a column from highest to lowest (tree-set auto sorts for us)
    		for( Vector3in v : set ) {

    			// get the chunk that is higher than the current one
    			// confusing as treeset is sorted in reverse, so lower actually
    			// means 'spatially higher' and vice versa...
    			Vector3in prev = set.lower( v );
    			
    			// only proceed if a higher chunk exists
    			if( prev != null )  {
    				
					Chunk chunk = this.chunkMap.get( v );
    				Chunk higherChunk = this.chunkMap.get( prev );
    				
    				// if the higher chunk has either been modified via a block change, or has had its occlusion map
    				// modified (perhaps by an even higher chunk), then we need to recalculate this chunk's occlusion map
    				if( ( higherChunk.state & ChunkState.DIRECTLY_DIRTY ) > 0 ) {

    					// loop through each cell of the occlusion map...
    					for( int x = 0; x < Config.CHUNK_DIM; x += 1 )
    					for( int z = 0; z < Config.CHUNK_DIM; z += 1 ) {
    						
    						// get the occlusion state for the current and higher chunk
    						// at this cell position
    						Vector3in col = new Vector3in( x, 0, z );
    						Occlusion higherOcclusion = higherChunk.getOcclusion( col );
    						Occlusion occlusion = chunk.getOcclusion( col );
    						
    						// save the current occlusion state so we can check later if its changed
    						Occlusion oldOcclusion = new Occlusion( occlusion );

    						// at the very least, the new occlusions state will be equal to the higher one
    						// and indeed it will actually be equal if the higher chunk has not undergone
    						// any direct block changes
    						occlusion.set( higherOcclusion );

    						// if the higher chunk has undergone some direct block changes
    						// we need to scan through the chunk and update the lower chunk's
    						// occlusion map where appropriate
    						if( ( higherChunk.state & ChunkState.DIRTY_BLOCK.flag  ) > 0 )
    						for( int y = 0; y < Config.CHUNK_DIM; y += 1 ) {

    							Block b = higherChunk.getBlockContext( new Vector3in( x,y,z ) ).block;
    							occlusion.particleOcclusion |= b.opacity != Opacity.INVISIBLE;
    							occlusion.lightOcclusion |= b.opacity == Opacity.OPAQUE;

    							// if occlusion is at max, no point looking further in this column
    							if( occlusion.particleOcclusion && occlusion.lightOcclusion )
    								break;
    						}
    						
    						// if the occlusion has changed, mark the chunk's state appropriately
    						if( !oldOcclusion.equals( occlusion ) )
    							chunk.state |= ChunkState.DIRTY_OCCLUSION.flag;
    						
    					}
    					
    				} 
    			}
    		}
    	}
    }
    
    private void markSurroundingChunks() {

    	for( Vector3in v : this.chunkMap.keySet() ) {
    		Chunk chunk = this.chunkMap.get( v );
    		// if a chunk has been modified via a block or occlusion change
    		// then mark its surrounding neighbours as indirectly dirty
    		if( ( chunk.state & ChunkState.DIRECTLY_DIRTY ) > 0 ) {
    			for( Vector3in.CubeNormal n : Vector3in.CubeNormal.values() ) {
    				// must create the chunk if it doesn't exist
    				Chunk neighbor = this.getChunk( v.add( n.vector ) );
    				neighbor.state |= ChunkState.DIRTY_NEIGHBOR.flag;
    			}
    		}
    	}

    	for( Vector3in v : this.chunkMap.keySet() ) {
    		Chunk chunk = this.chunkMap.get( v );
    		// if a chunk is indirectly dirty, mark its surrounding neighbors as requisite blocks
    		// as they will be needed in the lighting calcs despite their lighting vals not changing
    		if( ( chunk.state & ChunkState.DIRTY_NEIGHBOR.flag ) > 0 ) {
    			for( Vector3in.CubeNormal n : Vector3in.CubeNormal.values() ) {
    				Vector3in neighborVec = v.add( n.vector ); 
    				if( this.chunkMap.containsKey( neighborVec ))
    					this.chunkMap.get( neighborVec ).state |= ChunkState.REQUISITE.flag;
    			}
    		}
    	}

    }

    private void refreshLighting() {

    	// destroy the light values for all dirty chunks
    	for( Vector3in v : this.chunkMap.keySet() ) {
    		Chunk chunk = this.chunkMap.get( v );
    		if( ( chunk.state & ChunkState.DIRTY ) > 0 )
    			chunk.reset();
    	}

        // buffer of blocks that we need to propagate light from
        Set<Vector3in> toPropagate  = new HashSet<Vector3in>();

        // buffer to store updates to propagated (can't modify a collection while we iterate)
        Set<Vector3in> propagated = new HashSet<Vector3in>();

        // break the propagation up into chunk-sized bites so the buffers never grow super large
    	for( Vector3in chunkCoords : this.chunkMap.keySet() ) {
    		Chunk chunk = this.chunkMap.get( chunkCoords );
    		
    		// don't recalculate light for non-dirty chunks
    		if( ( chunk.state & ChunkState.DIRTY ) == 0 )
    			continue;
    		
    		// loop through every block in the chunk
    		for( int x = 0; x < Config.CHUNK_DIM; x += 1 )
    		for( int y = 0; y < Config.CHUNK_DIM; y += 1 )
    		for( int z = 0; z < Config.CHUNK_DIM; z += 1 ) {

    			// if the block is lit, add it to the propagation buffer...
    			Vector3in rawPos = new Vector3in(x,y,z);
    			if( chunk.getBlockContext( rawPos ).isLit() )
    				toPropagate.add( rawPos.add( chunkCoords.multiply( Config.CHUNK_DIM ) ) );
    		}


            // iterate the number of times light is allowed to jump
            for( int n = 0; n < Config.LIGHT_JUMPS; n += 1 ) {

                // for each of the 6 cube normals, propagate light outward
                // but only if the other block isn't opaque - otherwise the light is blocked
                for( Vector3in pos : toPropagate ) {
                    BlockContext b = this.getBlockContext( pos );
                    for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {
                    	Vector3in neighborVec = pos.add( normal.vector );
                        BlockContext other = this.getBlockContext( neighborVec );
                        if( other.block.opacity != Block.Opacity.OPAQUE ) {
                            other.propagate( b, Config.LIGHT_DROPOFF );
                            
                            // if we do propagate make sure we add the propagated block to the buffer
                            if( other.isLit() )
                                propagated.add( neighborVec );
                        }
                    }
                }

                //update the buffer with the changes 
                toPropagate.addAll( propagated );
                propagated.clear();
            }

            //clear the buffer when we're done with the propagation in preparation for the next chunk
            toPropagate.clear();
        }

    }
    
    private void buildCrossedBlock( Model model, Vector3in relCoords, Vector3in absCoords, BlockContext b ) {

    	// need 4 90 degree rotated quads
    	for( int i = 0; i < 360; i += 90 ) {

			matrix.clearMatrix();

    		// we want to offset the quads by 45 degrees so they appear diagonal so +45 to the yaw rotation
    		matrix.addYawToMatrix( i + 45 );

    		for( Vector3fl vertex : Model.QUAD_VERTICES ) {
    			
    			// push the template back to the origin so yaw rotations are centered
    			// scale and translate it so its re-ranged to [0,1]
    			Vector3fl vertexOffset = matrix.transform( vertex.add( new Vector3fl(0,0,-1)) ).multiply( 0.5f ).add( 0.5f );
				model.addAttributeData( AttributeVariable.POSITION, relCoords.toVector3fl().add( vertexOffset ) );

				// rerange the vertex ( we don't care z as its for 2d tex coords )
				Vector3fl rangedVertex = vertex.multiply( - 0.5f ).add( 0.5f );
				Vector3fl texCoords = b.block.texCoords.toVector3fl().add( rangedVertex ).multiply( Config.BLOCK_ATLAS_TEX_DIM );
				model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoords.divide( model.texture.size ) );

				// I feel like crosses shouldn't have occlusion but what do I know
				model.addAttributeData( AttributeVariable.SHADOW, 0 );

				// I also feel like normals are unecessary. I don't want to color orthogonal arms different colors as
				// it makes it feel more 2D paper cutout-y (IMO)
				model.addAttributeData( AttributeVariable.NORMAL, 0 );
				
				// feel like smooth lighting could potentially be applicable here
				// but its not crucial as crosses never touch ( 1 < 1.4 ) and so we never
				// see any gross discontinuities
				for( LightSource src : LightSource.values() )
					model.addAttributeData( src.attributeVariable, b.getIllumination(src).toPackedBytes() );
    		}
    		
			model.addQuad();

    	}
    	

    }

    private void buildOpaqueBlock( Model model, Vector3in relCoords, Vector3in absCoords, BlockContext b ) {

		// examine each face/quad of each block
		for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {
			
			matrix.clearMatrix();

			// if we are doing the back face, flip the template 180 degreees via yaw not pitch
			if( normal == Vector3in.CubeNormal.BACK )
				matrix.addYawToMatrix( 180 );

			// left and right faces can also be done by 90 degree yaw rotates
			else if( normal.vector.x != 0 )
				matrix.addYawToMatrix( 90 * normal.vector.x );
			
			// the only pitch rotation should be for top and bottom normal
			else if( normal.vector.y != 0 )
				matrix.addPitchToMatrix( -90 * normal.vector.y );

			// if the block touching the current face is opaque, then it is hidden and we should ignore
			Vector3in offsetVec = absCoords.add( normal.vector );
			BlockContext offsetBlock = this.getBlockContext( offsetVec );
			if( offsetBlock.block.opacity == Block.Opacity.OPAQUE )
				continue;
			
			for( Vector3fl vertex : Model.QUAD_VERTICES ) {
				
				// post rotation, the only thing left to do to the template is rerange it to [0,1]
				Vector3fl vertexOffset = matrix.transform( vertex ).multiply( 0.5f ).add( 0.5f );
				model.addAttributeData( AttributeVariable.POSITION, relCoords.toVector3fl().add( vertexOffset ) );

				// do reranging on raw template for tex-coords
				Vector3fl rangedVertex = vertex.multiply( 0.5f ).add( 0.5f );
				Vector3fl texCoords = b.block.texCoords.toVector3fl().add( rangedVertex ).multiply( Config.BLOCK_ATLAS_TEX_DIM );
				model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoords.divide( model.texture.size ) );

				// calculate neighbor block vectors by pushing the template to origin ( z = 0 ) and discarding none,one,both of (x,y)
				Vector3in side1Vec = matrix.transform( new Vector3fl( vertex.x, 0, 0 ) ).toRoundedVector3in().add( absCoords );
				Vector3in side2Vec = matrix.transform( new Vector3fl( 0, vertex.y, 0 ) ).toRoundedVector3in().add( absCoords );
				Vector3in cornerVec = matrix.transform( new Vector3fl( vertex.x, vertex.y, 0 ) ).toRoundedVector3in().add( absCoords );
				
				BlockContext side1Block = this.getBlockContext( side1Vec );
				BlockContext side1OffsetBlock = this.getBlockContext( side1Vec.add( normal.vector ) );

				BlockContext side2Block = this.getBlockContext( side2Vec );
				BlockContext side2OffsetBlock = this.getBlockContext( side2Vec.add( normal.vector ) );

				BlockContext cornerBlock = this.getBlockContext( cornerVec );
				BlockContext cornerOffsetBlock = this.getBlockContext( cornerVec.add( normal.vector ) );
				
				if( side1OffsetBlock.block.opacity == Opacity.OPAQUE && side2OffsetBlock.block.opacity == Opacity.OPAQUE )
					model.addAttributeData( AttributeVariable.SHADOW, 3 );
				else {
					int total = side1OffsetBlock.block.opacity == Opacity.OPAQUE ? 1 : 0;
					total += side2OffsetBlock.block.opacity == Opacity.OPAQUE ? 1 : 0;
					total += cornerOffsetBlock.block.opacity == Opacity.OPAQUE ? 1 : 0;
					model.addAttributeData( AttributeVariable.SHADOW, total );
				}
				
				
				for( LightSource src : LightSource.values() ) {
					Vector3in baseLight = offsetBlock.getIllumination(src);
					if( side1OffsetBlock.block.opacity != Opacity.OPAQUE && side1Block.block.opacity == Opacity.OPAQUE)
						baseLight = baseLight.max( side1OffsetBlock.getIllumination( src ));
					if( side2OffsetBlock.block.opacity != Opacity.OPAQUE && side2Block.block.opacity == Opacity.OPAQUE)
						baseLight = baseLight.max( side2OffsetBlock.getIllumination( src ));
					if( cornerOffsetBlock.block.opacity != Opacity.OPAQUE && cornerBlock.block.opacity == Opacity.OPAQUE )
						if( side1OffsetBlock.block.opacity != Opacity.OPAQUE || side2OffsetBlock.block.opacity != Opacity.OPAQUE )
							baseLight = baseLight.max( cornerOffsetBlock.getIllumination( src ));
					model.addAttributeData( src.attributeVariable, baseLight.toPackedBytes() );
				} 

				model.addAttributeData( AttributeVariable.NORMAL, normal.vector.toPackedBytes() );
			}


			// we've added four vertices, time to persist that as a quad
			model.addQuad();
		}

    }

    private void refreshModels() {

        // rebuild only the models of dirty chunks
    	for( Vector3in chunkCoords : this.chunkColumns.keySet() ) {
    		Chunk chunk = this.chunkMap.get( chunkCoords );

    		// at this point the model is updating, so any state can be zapped away
    		// and any previously non dirty chunks can then be ignored
    		int state = chunk.state;
    		chunk.state = 0;
    		if( ( state & ChunkState.DIRTY ) == 0 )
				continue;
    		
    		// if the chunk already has a model, destroy it in preparation for the new one
    		if( chunk.model != null )
    			chunk.model.destroy();

    		// create the new empty model
    		chunk.model = new Model();
            // grab the texture the model will be using and point the model at said texture atlas
            chunk.model.texture = TextureRef.BLOCK;

            // loop through each block of a dirty chunk
    		for( int x = 0; x < Config.CHUNK_DIM; x += 1 )
    		for( int y = 0; y < Config.CHUNK_DIM; y += 1 )
    		for( int z = 0; z < Config.CHUNK_DIM; z += 1 ) {
    			Vector3in raw = new Vector3in(x,y,z);
    			Vector3in coords = chunkCoords.multiply( Config.CHUNK_DIM ).add( raw );
    			BlockContext ctx = chunk.getBlockContext( raw );
    			// if the block is invisible, don't draw anything
    			if( ctx.block.opacity == Opacity.INVISIBLE )
    				continue;
            	if( ctx.block.opacity == Opacity.OPAQUE )
            		this.buildOpaqueBlock( chunk.model, raw, coords, ctx );
            	else if( ctx.block.opacity == Opacity.CROSSED )
            		this.buildCrossedBlock( chunk.model, raw, coords, ctx );
    		}

            // all the data has been collected by the model
            // now we can munge the data into buffers and shunt it off to VRAM
			chunk.model.buildModel();
			// update the chunks model via the rendering component
        }
    }

    // refresh the world - update lighting and models
    // of all dirty chunks. After this method has run
    // we can expect the terrain models to be in sync with
    // the underlying data
    public void refresh() {
    	this.propagateOcclusion();
    	this.markSurroundingChunks();
        this.refreshLighting();
        this.refreshModels();
    }

}
