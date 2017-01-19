package game.block;

import java.util.HashSet;
import java.util.Set;

import game.Config;

import util.Vector3i;
import util.Vector2i;

public class Chunk {

    // convert a bounded 3D vector into an integer
    private static int packCoords( Vector3i v ) {
        int x = v.x * Config.CHUNK_DIM * Config.CHUNK_DIM;
        int y = v.y * Config.CHUNK_DIM;
        return x + y + v.z;
    }

    // convert a bounded 2D vector into an integer
    private static int packCoords( Vector2i v ) {
        int x = v.x * Config.CHUNK_DIM;
        return x + v.z;
    }

    // convert a bounded 2D vector and a 3D normal (cube face) into an integer
    private static int packFaceCoords( Vector3i.Normal n, Vector2i v ) {
        int ndim = n.ordinal() * Config.CHUNK_DIM * Config.CHUNK_DIM;
        return ndim + packCoords( v );
    }

    private static Vector3i unpackCoords( Vector3i v, int p ) {
        int sq = Config.CHUNK_DIM * Config.CHUNK_DIM;
        int rem = p % sq;
        v.x = p / sq;
        v.y = rem / Config.CHUNK_DIM;
        v.x = rem % Config.CHUNK_DIM;
        return v;
    }

    // core block data of the chunk
    private Block[] blockData;

    // illumination values of faces of adjacent chunks
    private Illumination[] faceIlluminationData;

    // occlusion map (looking up) for just this chunk
    private boolean[] chunkOcclusionMap;

    // occlusion map (looking up) for this chunk and all chunks above it
    private boolean[] occlusionMap;

    private Set<Vector3i> lightingBuffer;

    public Chunk() {

        // compute array lengths
        int squareLen = Config.CHUNK_DIM * Config.CHUNK_DIM;
        int faceLen = squareLen * Vector3i.Normal.values().length;

        this.lightingBuffer = new HashSet<Vector3i>();

        // instance and fill chunk data arrays
        this.blockData = new Block[ squareLen * Config.CHUNK_DIM ];
        this.faceIlluminationData = new Illumination[ faceLen ];
        this.chunkOcclusionMap = new boolean[ squareLen ];
        this.occlusionMap = new boolean[ squareLen ];

        for( int i = 0; i < this.blockData.length; i += 1 )
            this.blockData[i] = new Block();
        for( int i = 0; i < this.faceIlluminationData.length; i += 1 )
            this.faceIlluminationData[i] = new Illumination();
        for( int i = 0; i < this.occlusionMap.length; i += 1 ) {
            this.occlusionMap[i] = false;
            this.chunkOcclusionMap[i] = false;
        }
    }

    // get a block from the chunk
    public Block getBlock( Vector3i v ) {
        return this.blockData[ packCoords( v ) ];
    }

    // get illumination values of block on chunk face
    public Illumination getExitIllumination( Vector3i.Normal n, Vector2i v ) {
        return this.faceIlluminationData[ packFaceCoords( n, v ) ];
    }

    // get illumination values of block on adjacent chunk face
    public Illumination getBorderIllumination( Vector3i.Normal n, Vector2i v ) {
        Vector3i expl = n.vector.clone()
            // map (-1,1) -> (0,CHUNK_DIM-1) for the non zero index (i.e. take us to a face)
            .transform( x -> Math.max( x * ( Config.CHUNK_DIM - 1 ), 0 ) )
            // explode the 2D vector into the remaining zero indices
            .explode( v, n );
        return this.blockData[ packCoords( expl ) ].illumination;
    }

    public void propagateLighting() {
        this.lightingBuffer.clear();
    }
}
