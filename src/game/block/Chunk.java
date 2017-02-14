package game.block;

import game.Config;
import game.gfx.Model;
import util.Vector3in;

// a container object that holds a fixed-size cube of terrain
// terrain is rendered in "chunks" as opposed to single cubes
public final class Chunk {

    // number of blocks in a chunk face
    private static final int DIM_SQ = Config.CHUNK_DIM * Config.CHUNK_DIM;

    // number of blocks in a chunk
    private static final int DIM_CB = DIM_SQ * Config.CHUNK_DIM;

    // convert a bounded position vector into an ix to index into our block array
    private static int pack( Vector3in v ) {
        int y = v.y * DIM_SQ;
        int x = v.x * Config.CHUNK_DIM;
        return y + x + v.z;
    }
    
    private static int pack2D( Vector3in v ) {
    	return v.z * Config.CHUNK_DIM + v.x;
    }

    // the raw block data
    private BlockContext[] blockData;
    private Occlusion[] occlusionData;
    public int state;
    public Model model;
    
    public Chunk() {
        super();

        // initialize an array to hold every single block
        this.blockData = new BlockContext[ DIM_CB ];
        this.occlusionData = new Occlusion[ DIM_SQ ];
        this.state = 0;

        // initialize each block as an empty/air block
        for( int i = 0; i < DIM_SQ; i += 1 )
        	this.occlusionData[i] = new Occlusion();
        for( int i = 0; i < DIM_CB; i += 1 )
            this.blockData[i] = new BlockContext();
    }

    // retrieve a block from the chunk
    public BlockContext getBlockContext( Vector3in v ) {
        return this.blockData[ pack( v ) ];
    }
    
    public Occlusion getOcclusion( Vector3in v ) {
    	return this.occlusionData[ pack2D( v ) ];
    }
    
    public void resetIllumination() {
        for( int i = 0; i < DIM_CB; i += 1 ) {
            BlockContext b = this.blockData[ i ];
            b.reset();
        }
    }

}
