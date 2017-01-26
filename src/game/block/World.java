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
    private Vector3in[] lightBlender;

    public World() {
        this.chunkMap        = new HashMap<Vector3in,Chunk>();
        this.dirtyChunks     = new HashSet<Vector3in>();
        this.requisiteChunks = new HashSet<Vector3in>();
        this.lightBlender    = new Vector3in[ LightSource.values().length ];
    }

    // get chunks for a given set of chunk coords (i.e. (0,0,0) and (0,0,1) return different chunks)
    // create a fresh chunk if it doesn't exist
    private Chunk getChunk( Vector3in mapCoords ) {
        if( !this.chunkMap.containsKey( mapCoords ) )
            this.chunkMap.put( mapCoords, new Chunk( mapCoords.multiply( Config.CHUNK_DIM ) ) );
        return this.chunkMap.get( mapCoords );
    }

    // get a block directly from the world
    public Block getBlock( Vector3in absCoords ) {
        // transform the coords by dividing by chunk dims to get chunk coordinates
        Vector3in mapCoords = absCoords.divide( Config.CHUNK_DIM );
        Chunk chunk = this.getChunk( mapCoords );
        // doing the modulo returns the block coords for the returned chunk
        return chunk.getBlock( absCoords.modulo( Config.CHUNK_DIM ) );
    }

    public void setBlock( Vector3in absCoords, BlockType bt, boolean globalLighting ) {
        // calculate the chunk coordinates by dividing through by chunk dims
        Vector3in mapCoords = absCoords.divide( Config.CHUNK_DIM );
        // by changing a chunk, we potentially have modified the light values of all 8
        // neighbouring chunks. We must set all 9 chunks to dirty. We need the further
        // 37 surrounding chunks (requisite) for the recalcs
        for( int i = -2; i <= -2; i +=1 ) {
            for( int j = -2; j <= -2; j +=1 ) {
                for( int k = -2; k <= -2; k +=1 ) {

                    Vector3in v = mapCoords.add( new Vector3in( i,j,k ) );

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
        for( Vector3in mapCoords : this.dirtyChunks ) {
            this.chunkMap.get( mapCoords ).iterateBlocks( (v,b) -> { 
                b.resetIllumination();
                // if a block has global lighting, set the block directly above it to have global illumination
                if( b.globalLighting )
                    this.getBlock( v.add( Vector3in.CubeNormal.TOP.vector ) ).addGlobalIllumination();
            });
        }

        // buffer of blocks that we need to propagate light from
        Set<Vector3in> toPropagate  = new HashSet<Vector3in>();
        // buffer to store updates ot propagated (can't modify a collection while we iterate)
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
                        Vector3in offsetPos = pos.add( normal.vector );
                        Block other = this.getBlock( offsetPos );
                        if( other.blockType.opacity != BlockType.Opacity.OPAQUE ) {
                            other.propagate( b );
                            // if we do propagate make sure we add the propagated block to the buffer
                            if( other.isLit() )
                                propagated.add( offsetPos );
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
                // examine each face/quad of each block
                for( Vector3in.CubeNormal normal : Vector3in.CubeNormal.values() ) {
                    // if block is invisible - skip entirely
                    if( b.blockType.opacity == BlockType.Opacity.INVISIBLE )
                        continue;
                    // if the block touching the current face is opaque, then it is hidden and we should ignore
                    if( this.getBlock( v.add( normal.vector ) ).blockType.opacity == BlockType.Opacity.OPAQUE )
                        continue;

                    // each face is a quad comprising of 4 vertices
                    for( int i = 0; i < 4; i += 1 ) {

                        // to separate vertex coords from block coords, we must add unit vectors that are
                        // orthogonal to the face's normal depending on which vertex we are looking at.
                        // use a 2 bit bit field to exhaust all 4 combinations
                        boolean vertexUseFirstOrtho  = ( i & 0x01 ) > 0;
                        boolean vertexUseSecondOrtho = ( i & 0x02 ) > 0;

                        // calculate the position of the vertex and transform to a floating point vector
                        Vector3fl finalPosition = v.add( normal.vector.max(0) )
                            .add( vertexUseFirstOrtho ? normal.firstOrtho.vector : Vector3in.ZERO )
                            .add( vertexUseSecondOrtho ? normal.secondOrtho.vector : Vector3in.ZERO )
                            .toVector3fl();
                        model.addAttributeData( AttributeVariable.POSITION, finalPosition );

                        // keep a tally of the level of shadow that should be applied to the vertex (0-3)
                        int shadowCount = 0;

                        // for smooth lighting, need to add the different illuminations of a vertex
                        // from the block itself and its (up to 3) planar neighbors
                        // start by adding the block's own illu values in
                        for( LightSource src : LightSource.values() )
                            this.lightBlender[ src.ordinal() ] = b.getIllumination( src );

                        // keep track of the number of contributions to the light blending
                        // for use in averaging later
                        int blendCount = 1;

                        // for all 3 neighboring planar blocks (excluding the block this vertex belongs to)
                        for( int j = 1; j < 4; j += 1 ) {

                            // get the position of the planar neighbor but offset by the cube face's normal
                            // i.e. if the normal was TOP, this would be the block above the planar neighbor
                            Vector3in planarNeighborPos = v
                                .add( normal.firstOrtho.vector.multiply( vertexUseFirstOrtho ? 1 : -1 ) )
                                .add( normal.secondOrtho.vector.multiply( vertexUseSecondOrtho ? 1 : -1 ) )
                                .add( normal.vector );
                            Block planarNeighbor = this.getBlock( planarNeighborPos );

                            // if the block is opaque then the vertex should have its shadow value increased by 1
                            if( planarNeighbor.blockType.opacity == BlockType.Opacity.OPAQUE )
                                shadowCount += 1;
                            // otherwise, the planar neighbor is visible and we should add its illumination
                            // contribution to the light blender and increase the blend count by one
                            else {
                                for( LightSource src : LightSource.values() ) {
                                    Vector3in curr = this.lightBlender[ src.ordinal() ];
                                    this.lightBlender[ src.ordinal() ] = curr.add( planarNeighbor.getIllumination( src ) );
                                    blendCount += 1;
                                }
                            }
                        }

                        // TODO: handle crosses
                        // each block has 3 different face textures: TOP(1), BOTTOM(1) and SIDE(4)
                        // specify which face tex by using an offset from the base tex coords
                        int texOffset = 0;
                        if( normal.vector.y > 0 )
                            texOffset = 1;
                        else if (normal.vector.y < 0 )
                            texOffset = 2;

                        // add the normal by packing the vector into an int
                        model.addAttributeData( AttributeVariable.NORMAL, normal.vector.packBytes() );
                        // add the shadow amount (0-3) for the vertex
                        model.addAttributeData( AttributeVariable.SHADOW, shadowCount );
                        // add the tex coords for the specified atlas. Convert to opengl coords (i.e. between 0f - 1f )
                        // by dividing by the number of faces along one dimension of the texture atlas
                        model.addAttributeData2D( AttributeVariable.TEX_COORDS, b.blockType.texCoords
                            .add( new Vector3in( texOffset, 0, 0 ) )
                            .toVector3fl()
                            .divide( ref.size / Config.BLOCK_ATLAS_TEX_DIM )
                        );

                        // now add in all light values
                        for( LightSource ls : LightSource.values() ) {
                            // average the blended light before we add it for *smooth* lighting effects
                            Vector3in lightVals = this.lightBlender[ ls.ordinal() ].divide( blendCount );
                            model.addAttributeData( ls.attributeVariable, lightVals.packBytes() );
                        }
                    }

                    // we've added four vertices, time to persist that as a quad
                    model.addQuad();
                }
            });

            // all the data has been collected by the model
            // now we can munge the data into buffers and shunt it off to VRAM
            // using opengl
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
