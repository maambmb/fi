package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    
    private static int columnCompare( Vector3in l, Vector3in r ) {
    	return r.y - l.y;
    }
 
    private static Vector3in partition( Vector3in v ) {
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
    private Map<Vector3in,TreeSet<Vector3in>> chunkColumns;
    
    public World() {
        this.chunkMap        = new HashMap<Vector3in,Chunk>();
        this.chunkColumns    = new HashMap<Vector3in,TreeSet<Vector3in>>();
    }

    private Chunk getChunk( Vector3in mapCoords ) {
        if( !this.chunkMap.containsKey( mapCoords ) ) {
            this.chunkMap.put( mapCoords, new Chunk() );
        	Vector3in col = new Vector3in( mapCoords.x, 0, mapCoords.z );
        	if( !this.chunkColumns.containsKey( col ) )
        		this.chunkColumns.put( col, new TreeSet<Vector3in>( World::columnCompare ) );
        	TreeSet<Vector3in> list = this.chunkColumns.get( col );
        	list.add( mapCoords );
        }
        return this.chunkMap.get( mapCoords );
    }
    
    private BlockContext getBlockContext( Vector3in absCoords ) {
    	Vector3in mapCoords = partition( absCoords );
        Chunk chunk = this.getChunk( mapCoords );
        return chunk.getBlockContext(mapCoords.multiply( - Config.CHUNK_DIM ).add( absCoords ));
    }
    
    public Block getBlock( Vector3in absCoords ) {
    	return this.getBlockContext( absCoords ).block;
    }

    public void setBlock( Vector3in absCoords, Block bt ) {

        // calculate the chunk coordinates by dividing through by chunk dims
    	Vector3in mapCoords = partition( absCoords );
        Chunk chunk = this.getChunk( mapCoords );
        BlockContext ctx = chunk.getBlockContext(mapCoords.multiply( - Config.CHUNK_DIM ).add( absCoords ));

        if( ctx.block != bt )
        	chunk.state |= ChunkState.DIRECT_BLOCK.flag;

        ctx.block = bt;
    }
    
    private void propagate() {

    	for( TreeSet<Vector3in> set : this.chunkColumns.values() ) {
    		for( Vector3in v : set ) {
    			Vector3in prev = set.lower( v );
    			Chunk chunk = this.getChunk( v );
    			if( prev != null )  {
    				
    				Chunk prevChunk = this.getChunk( prev );
    				if( ( prevChunk.state & ( ChunkState.DIRECT_BLOCK.flag | ChunkState.OCCLUSION.flag ) ) > 0 ) {

    					for( int x = 0; x < Config.CHUNK_DIM; x += 1 )
    					for( int z = 0; z < Config.CHUNK_DIM; z += 1 ) {
    						
    						Vector3in col = new Vector3in( x, 0, z );
    						Occlusion prevOccl = prevChunk.getOcclusion( col );

    						Occlusion occl = chunk.getOcclusion( col );
    						Occlusion oldOccl = new Occlusion( occl );
    						occl.set( prevOccl );

    						if( ( prevChunk.state & ChunkState.OCCLUSION.flag  ) > 0 )
    						for( int y = 0; y < Config.CHUNK_DIM; y += 1 ) {

    							Block b = prevChunk.getBlockContext( new Vector3in( x,y,z ) ).block;
    							occl.particleOcclusion |= b.opacity != Opacity.INVISIBLE;
    							occl.lightOcclusion |= b.opacity == Opacity.OPAQUE;

    							if( occl.particleOcclusion && occl.lightOcclusion )
    								break;
    						}
    						
    						if( oldParticleOccl != occl.particleOcclusion || oldLightOccl != occl.lightOcclusion )
    							chunk.state |= ChunkState.OCCLUSION.flag;
    						
    					}
    					
    				} else if( ( prevChunk.state | ChunkState.OCCLUSION.flag ) > 0 ) {
    					
    				}

    		}
    	}

    }
    
    
    private void markSurroundingChunks() {
    	
    	for( Vector3in v : this.dirtyChunks ) {
    		for( int x = -2; x <= 2; x += 1 )
    		for( int y = -2; y <= 2; y += 1 )
    		for( int z = -2; z <= 2; z += 1 ) {
    			Vector3in rawOffset = new Vector3in(x,y,z);
    			Vector3in chunkCoords = v.add( rawOffset ); 
    			if( rawOffset.toMaxElement() <= 1 && rawOffset.toMinElement() >= -1 ) {
    				this.getChunk( chunkCoords );
    				this.dirtyBuffer.add( chunkCoords );
    			}
    			if( this.chunkMap.containsKey( chunkCoords ) )
    				this.requisiteChunks.add( v );
    		}
    	}
    	this.dirtyChunks.addAll( this.dirtyBuffer );
    	this.dirtyBuffer.clear();
    }

    private void refreshLighting() {

        // dirty chunks need to be recalculated from scratch
        // so we clear all illumination values and set them to the block type's
        // natural illumination
        for( Vector3in mapCoords : this.dirtyChunks )
            this.chunkMap.get( mapCoords ).reset();

        // buffer of blocks that we need to propagate light from
        Set<Vector3in> toPropagate  = new HashSet<Vector3in>();

        // buffer to store updates to propagated (can't modify a collection while we iterate)
        Set<Vector3in> propagated = new HashSet<Vector3in>();

        // break the propagation up into chunk-sized bites so the buffers never grow super large
        for( Vector3in mapCoords : this.requisiteChunks ) {

            // add only the naturally illuminating blocks into the buffer
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> {
                if( b.isLit() )
                    toPropagate.add( v.add( mapCoords.multiply(Config.CHUNK_DIM ) ) );
            });

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


    	for( int i = 0; i < 360; i += 90 ) {

			matrix.clearMatrix();
    		matrix.addYawToMatrix( i + 45 );

    		for( Vector3fl vertex : Model.QUAD_VERTICES ) {
    			Vector3fl vertexOffset = matrix.transform( vertex.add( new Vector3fl(0,0,-1)) ).multiply( 0.5f );
    			vertexOffset = vertexOffset.add( 0.5f );
				model.addAttributeData( AttributeVariable.POSITION, relCoords.toVector3fl().add( vertexOffset ) );

				Vector3fl rangedVertex = vertex.multiply( - 0.5f ).add( 0.5f );
				Vector3fl texCoords = b.block.texCoords.toVector3fl().add( rangedVertex ).multiply( Config.BLOCK_ATLAS_TEX_DIM );
				model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoords.divide( model.texture.size ) );

				model.addAttributeData( AttributeVariable.SHADOW, 0 );
				
				for( LightSource src : LightSource.values() )
					model.addAttributeData( src.attributeVariable, b.getIllumination(src).toPackedBytes() );

				// add the shadow amount (0-3) for the vertex
				// add the normal by packing the vector into an int
				model.addAttributeData( AttributeVariable.NORMAL, 0 );

    		}
    		
			model.addQuad();

    	}
    	

    }

    private void buildOpaqueBlock( Model model, Vector3in relCoords, Vector3in absCoords, BlockContext b ) {

		// examine each face/quad of each block
		for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {
			
			matrix.clearMatrix();

			if( normal.vector.z < 0 )
				matrix.addYawToMatrix( 180 );
			else if( normal.vector.x != 0 )
				matrix.addYawToMatrix( 90 * normal.vector.x );
			else if( normal.vector.y != 0 )
				matrix.addPitchToMatrix( -90 * normal.vector.y );

			// if the block touching the current face is opaque, then it is hidden and we should ignore
			Vector3in offsetVec = absCoords.add( normal.vector );
			BlockContext offsetBlock = this.getBlockContext( offsetVec );
			if( offsetBlock.block.opacity == Block.Opacity.OPAQUE )
				continue;
			
			// each face is a quad comprising of 4 vertices
			for( Vector3fl vertex : Model.QUAD_VERTICES ) {
				
				Vector3fl vertexOffset = matrix.transform( vertex ).multiply( 0.5f ).add( 0.5f );
				model.addAttributeData( AttributeVariable.POSITION, relCoords.toVector3fl().add( vertexOffset ) );

				Vector3fl rangedVertex = vertex.multiply( 0.5f ).add( 0.5f );
				Vector3fl texCoords = b.block.texCoords.toVector3fl().add( rangedVertex ).multiply( Config.BLOCK_ATLAS_TEX_DIM );
				model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoords.divide( model.texture.size ) );

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

				// add the shadow amount (0-3) for the vertex
				// add the normal by packing the vector into an int
				model.addAttributeData( AttributeVariable.NORMAL, normal.vector.toPackedBytes() );
			}


			// we've added four vertices, time to persist that as a quad
			model.addQuad();
		}

    }

    private void refreshModels() {

        // rebuild only the models of dirty chunks
        for( Vector3in mapCoords: this.dirtyChunks ) {

            // create the empty chunk model and choose the subset of attribute variables that will be used
            // this model is terrain so we should use the vars that the block shader uses...
            Model model = new Model();

            // grab the texture the model will be using and point the model at said texture atlas
            model.texture = TextureRef.BLOCK;

            // loop through each block of a dirty chunk
            Chunk chunk = this.chunkMap.get( mapCoords );
            chunk.iterateBlocks( (v,b) -> {
            	if( b.blockType.opacity == Block.Opacity.INVISIBLE )
            		return;
            	if( b.blockType.opacity == Block.Opacity.OPAQUE )
            		this.buildOpaqueBlock( model, v, v.add( mapCoords.multiply( Config.CHUNK_DIM ) ), b);
            	else if( b.blockType.opacity == Block.Opacity.CROSSED )
            		this.buildCrossedBlock( model, v, v.add( mapCoords.multiply( Config.CHUNK_DIM ) ), b);
            });

            // all the data has been collected by the model
            // now we can munge the data into buffers and shunt it off to VRAM
			model.buildModel();
			// update the chunks model via the rendering component
			chunk.renderCmpt.model = model;

        }
    }

    // refresh the world - update lighting and models
    // of all dirty chunks. After this method has run
    // we can expect the terrain models to be in sync with
    // the underlying data
    public void refresh() {
        this.refreshLighting();
        this.refreshModels();
        this.dirtyChunks.clear();
        this.requisiteChunks.clear();
    }

}
