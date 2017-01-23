package game.block;

import game.Config;

import util.Lambda;
import util.Vector3i;

// a container object that holds a fixed-size cube of terrain
// terrain is rendered in "chunks" as opposed to single cubes
public class Chunk {

    // number of blocks in a chunk face
    private static final int DIM_SQ = Config.CHUNK_DIM * Config.CHUNK_DIM;
    // number of blocks in a chunk
    private static final int DIM_CB = DIM_SQ * Config.CHUNK_DIM;

    // convert a bounded position vector into an ix to index into our block array
    private static int pack( Vector3i v ) {
        int y = v.y * DIM_SQ;
        int x = v.x * Config.CHUNK_DIM;
        return y + x + v.z;
    }

    // convert an ix back into a position vector
    private static Vector3i unpack( int p ) {
        int rem = p % DIM_SQ;
        int y = p / DIM_SQ;
        int x = rem / Config.CHUNK_DIM;
        int z = rem % Config.CHUNK_DIM;
        return new Vector3i( x,y,z );
    }

    // the raw block data
    private Block[] blockData;

    public Chunk() {

        // initialize an array to hold every single block
        this.blockData = new Block[ DIM_CB ];

        // initialize each block as an empty/air block
        for( int i = 0; i < DIM_CB; i += 1 )
            this.blockData[i] = new Block();
    }

    // retrieve a block from the chunk
    public Block getBlock( Vector3i v ) {
        return this.blockData[ pack( v ) ];
    }

    // given an iterator fn, run it on every single block
    public void iterateBlocks( Lambda.ActionBinary<Vector3i,Block> fn ) {
        for( int i = 0; i < DIM_CB; i += 1 ) {
            Vector3i v = unpack( i );
            fn.run( v, this.blockData[i] );
        }
    }

    public void reset() {
        for( int i = 0; i < DIM_CB; i += 1 ) {
            Block b = this.blockData[ i ];
            b.blockType = BlockType.AIR;
            b.illumination.reset();
        }
    }

}
