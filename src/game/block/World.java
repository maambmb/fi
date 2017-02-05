package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Config;
import game.block.BlockType.Opacity;
import game.gfx.AtlasLoader;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.shader.BlockShader;

import util.Vector3fl;
import util.Vector3in;

public class World {

    // holds the chunks that comprise of the visible (and beyond) game world
    // is the main interface/access for getting/setting blocks
    // allows recomputation of lighting/models upon modification in a relatively efficient batched manner

    public static World WORLD;
    public static void init() {
        WORLD = new World();
    }

    // (sparse) hashmap of all active chunks
    private Map<Vector3in,Chunk> chunkMap;

    // collection of chunks that have been modified and need to have their lighting and models recalced
    private Set<Vector3in> dirtyChunks;

    // collection of chunks that need to recompute their lighting
    // not because the chunks are dirty, but because we expect their lighting to overflow onto nearby
    // dirty chunks that themselves need recalcs ( should border all dirty chunks )
    private Set<Vector3in> requisiteChunks;

    // a buffer to store the sums of illuminations from multiple different blocks
    // for the purposes of averaging for smooth lighting
    private static Vector3in[] lightBlender;

    public World() {
        this.chunkMap        = new HashMap<Vector3in,Chunk>();
        this.dirtyChunks     = new HashSet<Vector3in>();
        this.requisiteChunks = new HashSet<Vector3in>();
    }
    
    private static Vector3in getMapCoords( Vector3in absCoords ) {
    	Vector3in div = absCoords.divide( Config.CHUNK_DIM );
    	if( absCoords.x < 0 && absCoords.x % Config.CHUNK_DIM != 0 )
    		div.x -= 1;
    	if( absCoords.y < 0 && absCoords.y % Config.CHUNK_DIM != 0 )
    		div.y -= 1;
    	if( absCoords.z < 0 && absCoords.z % Config.CHUNK_DIM != 0 )
    		div.z -= 1;
    	return div;
    }

    private Chunk getChunk( Vector3in mapCoords ) {
        if( !this.chunkMap.containsKey( mapCoords ) ) {
        	Chunk c = new Chunk();
        	c.positionCmpt.position = mapCoords.multiply(Config.CHUNK_DIM ).toVector3fl();
            this.chunkMap.put( mapCoords, c );
        }
        return this.chunkMap.get( mapCoords );
    }

    public Block getBlock( Vector3in absCoords ) {
    	Vector3in mapCoords = getMapCoords( absCoords );
        Chunk chunk = this.getChunk( mapCoords );
        return chunk.getBlock(absCoords.subtract( mapCoords.multiply( Config.CHUNK_DIM ) ));
    }

    public void setBlock( Vector3in absCoords, BlockType bt ) {

        // calculate the chunk coordinates by dividing through by chunk dims
    	Vector3in mapCoords = getMapCoords( absCoords );

        // by changing a chunk, we potentially have modified the light values of all 8
        // neighbouring chunks. We must set all 9 chunks to dirty. We need the further
        // 37 surrounding chunks (requisite) for the recalcs
        for( int i = -2; i <= 2; i +=1 ) {
            for( int j = -2; j <= 2; j +=1 ) {
                for( int k = -2; k <= 2; k +=1 ) {

                	Vector3in rawOffset = new Vector3in(i,j,k);
                    Vector3in offsetMapCoords = mapCoords.add( rawOffset );

                    if( rawOffset.toMaxElement() <= 1 && rawOffset.toMinElement() >= -1 ) {
                        this.getChunk( offsetMapCoords );
                        this.dirtyChunks.add( offsetMapCoords );
                    }

                    // the outer 37 chunks are requisite (but only if 
                    // they exist - don't make them if we don't need them)
                    if( this.chunkMap.containsKey( offsetMapCoords ) )
                        this.requisiteChunks.add( offsetMapCoords );
                }
            }
        }

        // grab the actual chunk we want to modify, then grab the block and adjust the values
        Chunk chunk = this.getChunk( mapCoords );
        Block block = chunk.getBlock( absCoords.subtract( mapCoords.multiply( Config.CHUNK_DIM)));
        block.blockType = bt;
    }

    private void refreshLighting() {

        // dirty chunks need to be recalculated from scratch
        // so we clear all illumination values and set them to the block type's
        // natural illumination
        for( Vector3in mapCoords : this.dirtyChunks )
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> b.resetIllumination() );


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
                    Block b = this.getBlock( pos );
                    for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {
                    	Vector3in neighborVec = pos.add( normal.vector );
                        Block other = this.getBlock( neighborVec );
                        if( other.blockType.opacity != BlockType.Opacity.OPAQUE ) {
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


    private void refreshModels() {

        // rebuild only the models of dirty chunks
        for( Vector3in mapCoords: this.dirtyChunks ) {

            // create the empty chunk model and choose the subset of attribute variables that will be used
            // this model is terrain so we should use the vars that the block shader uses...
            Model model = new Model();
            for( AttributeVariable av : BlockShader.USED_ATTRIBUTE_VARS )
                model.initAttributeVariable( av );

            // grab the texture the model will be using and point the model at said texture atlas
            AtlasLoader.TextureRef ref = AtlasLoader.GLOBAL.getTexture( Config.BLOCK_ATLAS );
            model.atlasId = ref.id;

            // loop through each block of a dirty chunk
            Chunk chunk = this.chunkMap.get( mapCoords );
            chunk.iterateBlocks( (v,b) -> {

            	Vector3in absCoords = v.add ( mapCoords.multiply( Config.CHUNK_DIM ) );
                // examine each face/quad of each block
                for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {

                    // if block is invisible - skip entirely
                    if( b.blockType.opacity == BlockType.Opacity.INVISIBLE )
                        continue;

                    // if the block touching the current face is opaque, then it is hidden and we should ignore
                    Vector3in offsetVec = absCoords.add( normal.vector );
                    Block offsetBlock = this.getBlock( offsetVec );
                    if( offsetBlock.blockType.opacity == BlockType.Opacity.OPAQUE )
                        continue;
                    
                    // each face is a quad comprising of 4 vertices
                    for( int i = 0; i < 4; i += 1 ) {
                    	
                    	boolean vertexIsFarSide1 = ( i & 0x01 ) > 0;
                    	boolean vertexIsFarSide2 = ( i & 0x02 ) > 0;
                    	

                        // now using the bools above, calculate the vertex's position and add it to the model
						model.addAttributeData( AttributeVariable.POSITION, v.add( normal.vector.max( 0 ) )
								.add( normal.firstOrtho.vector.multiply( vertexIsFarSide1 ? 1 : 0 ) )
								.add( normal.secondOrtho.vector.multiply( vertexIsFarSide2 ? 1 : 0 ) )
								.toVector3fl() );

                        // add the tex coords for the specified atlas. Convert to opengl coords (i.e. between 0f - 1f )
                        // by dividing by the number of faces along one dimension of the texture atlas
                        Vector3in texCoords = new Vector3in( vertexIsFarSide1 ? 1 : 0, vertexIsFarSide2 ? 1 : 0, 0 );
                        Vector3fl texCoordsFl = texCoords.add( b.blockType.texCoords ).multiply( Config.BLOCK_ATLAS_TEX_DIM ).toVector3fl().divide( ref.size );
                        model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoordsFl );
						
						Vector3in side1Vec = absCoords.add( normal.firstOrtho.vector.multiply( vertexIsFarSide1 ? 1 : -1 ) );
						Block side1Block = this.getBlock( side1Vec );
						Block side1OffsetBlock = this.getBlock( side1Vec.add( normal.vector ) );

						Vector3in side2Vec = absCoords.add( normal.secondOrtho.vector.multiply( vertexIsFarSide2 ? 1 : -1 ) );
						Block side2Block = this.getBlock( side2Vec );
						Block side2OffsetBlock = this.getBlock( side2Vec.add( normal.vector ) );

						Vector3in cornerVec = absCoords
								.add( normal.firstOrtho.vector.multiply( vertexIsFarSide1 ? 1 : -1 ) )
								.add( normal.secondOrtho.vector.multiply( vertexIsFarSide2 ? 1 : -1 ) );
						Block cornerBlock = this.getBlock( cornerVec );
						Block cornerOffsetBlock = this.getBlock( cornerVec.add( normal.vector ) );
						
                        if( side1OffsetBlock.blockType.opacity == Opacity.OPAQUE && side2OffsetBlock.blockType.opacity == Opacity.OPAQUE )
							model.addAttributeData( AttributeVariable.SHADOW, 3 );
                        else {
                        	int total = side1OffsetBlock.blockType.opacity == Opacity.OPAQUE ? 1 : 0;
                        	total += side2OffsetBlock.blockType.opacity == Opacity.OPAQUE ? 1 : 0;
                        	total += cornerOffsetBlock.blockType.opacity == Opacity.OPAQUE ? 1 : 0;
							model.addAttributeData( AttributeVariable.SHADOW, total );
                        }
                        
                        
                        for( LightSource src : LightSource.values() ) {
                        	Vector3in baseLight = offsetBlock.getIllumination(src);
							if( side1OffsetBlock.blockType.opacity != Opacity.OPAQUE && side1Block.blockType.opacity == Opacity.OPAQUE)
								baseLight = baseLight.max( side1OffsetBlock.getIllumination( src ));
							if( side2OffsetBlock.blockType.opacity != Opacity.OPAQUE && side2Block.blockType.opacity == Opacity.OPAQUE)
								baseLight = baseLight.max( side2OffsetBlock.getIllumination( src ));
							if( cornerOffsetBlock.blockType.opacity != Opacity.OPAQUE && cornerBlock.blockType.opacity == Opacity.OPAQUE )
								if( side1OffsetBlock.blockType.opacity != Opacity.OPAQUE || side2OffsetBlock.blockType.opacity != Opacity.OPAQUE )
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
            });

            // all the data has been collected by the model
            // now we can munge the data into buffers and shunt it off to VRAM
            // using opengl (but only if there is stuff vertices to draw!)

            if( model.vertexCount > 0 ) {
                model.buildModel();

                // update the chunks model via the rendering component
                chunk.renderCmpt.model = model;
            }

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
