package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Config;
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

        lightBlender = new Vector3in[ LightSource.values().length ];
        for( int i = 0; i< lightBlender.length; i += 1 )
            lightBlender[i] = new Vector3in();

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
    		div.x += 1;
    	if( absCoords.y < 0 && absCoords.y % Config.CHUNK_DIM != 0 )
    		div.y += 1;
    	if( absCoords.z < 0 && absCoords.z % Config.CHUNK_DIM != 0 )
    		div.z += 1;
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
        if( chunk == null)
        	return null;
        return chunk.getBlock( absCoords.modulo( Config.CHUNK_DIM ).abs() );
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
        Block block = chunk.getBlock( absCoords.modulo( Config.CHUNK_DIM ).abs() );
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
                    toPropagate.add( v );
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
                            other.propagate( b );
                            
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
            AtlasLoader.TextureRef ref = AtlasLoader.LOADER.getTexture( Config.BLOCK_ATLAS );
            model.atlasId = ref.id;

            // loop through each block of a dirty chunk
            Chunk chunk = this.chunkMap.get( mapCoords );
            chunk.iterateBlocks( (v,b) -> {

				Vector3in absCoords = mapCoords.multiply( Config.CHUNK_DIM).add( v );

                // examine each face/quad of each block
                for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {

                    // if block is invisible - skip entirely
                    if( b.blockType.opacity == BlockType.Opacity.INVISIBLE )
                        continue;

                    // if the block touching the current face is opaque, then it is hidden and we should ignore
                    Vector3in neighborVec = absCoords.add( normal.vector );
                    if( this.getBlock( neighborVec ).blockType.opacity == BlockType.Opacity.OPAQUE )
                        continue;
                    
                    if( normal == Vector3in.CubeNormal.LEFT || normal == Vector3in.CubeNormal.RIGHT ) {
                    }
                    
                    // each face is a quad comprising of 4 vertices
                    for( int i = 0; i < 4; i += 1 ) {

                        // to separate vertex coords from block coords, we must add unit vectors that are
                        // orthogonal to the face's normal depending on which vertex we are looking at.
                        // use a 2 bit bit field to exhaust all 4 combinations
                        boolean vertexUseFirstOrtho  = ( i & 0x01 ) > 0;
                        boolean vertexUseSecondOrtho = ( i & 0x02 ) > 0;

                        Vector3in vertexVec = normal.vector.max(0).add( absCoords );
                        if( vertexUseFirstOrtho )
                        	vertexVec = vertexVec.add( normal.firstOrtho.vector );
                        if( vertexUseSecondOrtho )
                        	vertexVec = vertexVec.add( normal.secondOrtho.vector );
                        model.addAttributeData( AttributeVariable.POSITION, vertexVec.toVector3fl() );

                        // keep a tally of the level of shadow that should be applied to the vertex (0-3)
                        int shadowCount = 0;

                        // for smooth lighting, need to add the different illuminations of a vertex
                        // from the block itself and its (up to 3) planar neighbors
                        // start by adding the block's own illu values in
                        for( LightSource src : LightSource.values() )
                        	lightBlender[src.ordinal()] = b.getIllumination(src);

                        // keep track of the number of contributions to the light blending
                        // for use in averaging later
                        int blendCount = 1;

                        // for all 3 neighboring planar blocks (excluding the block this vertex belongs to)
                        for( int j = 1; j < 4; j += 1 ) {

                            // get the position of the planar neighbor but offset by the cube face's normal
                            // i.e. if the normal was TOP, this would be the block above the planar neighbor
                        	Vector3in planarNeighborVec = absCoords
                        			.add( normal.firstOrtho.vector.multiply( vertexUseFirstOrtho ? 1 : -1 ) )
                        			.add( normal.secondOrtho.vector.multiply( vertexUseSecondOrtho ? 1 : -1 ) );

                            // if the block is opaque then the vertex should have its shadow value increased by 1
                            if( this.getBlock( planarNeighborVec.add( normal.vector ) ).blockType.opacity == BlockType.Opacity.OPAQUE )
                                shadowCount += 1;

                            // otherwise, the planar neighbor is visible and we should add its illumination
                            // contribution to the light blender and increase the blend count by one
                            else {
                            	Block planarNeighbor = this.getBlock( planarNeighborVec );
                                for( LightSource src : LightSource.values() ) {
                                    lightBlender[src.ordinal()] = planarNeighbor.getIllumination( src ).add( lightBlender[ src.ordinal() ]);
                                    blendCount += 1;
                                }
                            }
                        }

                        // add the normal by packing the vector into an int
                        model.addAttributeData( AttributeVariable.NORMAL, normal.vector.toPackedBytes() );

                        // add the shadow amount (0-3) for the vertex
                        model.addAttributeData( AttributeVariable.SHADOW, shadowCount );

                        // add the tex coords for the specified atlas. Convert to opengl coords (i.e. between 0f - 1f )
                        // by dividing by the number of faces along one dimension of the texture atlas
                        
                        Vector3in texCoords = new Vector3in( vertexUseFirstOrtho ? 1 : 0, vertexUseSecondOrtho ? 1 : 0, 0 );
                        Vector3fl texCoordsFl = texCoords.add( b.blockType.texCoords ).multiply( Config.BLOCK_ATLAS_TEX_DIM ).toVector3fl().divide( ref.size );
                        model.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoordsFl );

                        // now add in all light values
                        for( LightSource ls : LightSource.values() ) {
                            // average the blended light before we add it for *smooth* lighting effects
                            Vector3in illu = lightBlender[ls.ordinal()].divide( blendCount );
                            model.addAttributeData( ls.attributeVariable, illu );
                        }
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
