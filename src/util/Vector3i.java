package util;

// an immutable 3D integer vector class
public class Vector3i {

    // the normals to all 6 faces of a cube
    public enum CubeNormal {
        
        FRONT( new Vector3i(0,0,-1) ),
        BACK( new Vector3i(0,0,1) ),
        LEFT( new Vector3i(-1,0,1) ),
        RIGHT( new Vector3i(1,0,0) ),
        TOP( new Vector3i(0,-1,0) ),
        BOTTOM( new Vector3i(0,1,0) );

        public Vector3i vector; 

        private CubeNormal( Vector3i v ) {
            this.vector = v;
        }
    }
    
    // zero sized vector
    public static final Vector3i ZERO = new Vector3i(0,0,0);

    // the 3 elements of the vector
    public final int x;
    public final int y;
    public final int z;

    // a hashcoder utility object for equality calcs
    private HashCoder hashCoder;
        
    // most straightforward to create a vector (specify the 3 elements)
    public Vector3i( int x, int y, int z ) {
        this.hashCoder = new HashCoder();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // specify the elements via a packed integer. Similar to how a hexcode specifies r,g,b values
    public Vector3i( int packed ) {
        this.hashCoder = new HashCoder();
        this.x = 0xFF & packed;
        packed >>= 8;
        this.y = 0xFF & packed;
        packed >>= 8;
        this.z = 0xFF & packed;
    }

    // transform each element of the vector by some unary x -> y fn (returns a new vector)
    public Vector3i transform( Lambda.FuncUnary<Integer,Integer> unaryFn ) {
        int x = unaryFn.run( this.x );
        int y = unaryFn.run( this.y );
        int z = unaryFn.run( this.z );
        return new Vector3i( x,y,z );
    }

    // transform each element of the vector by some binary x,y -> z fn, where the y values come from the other vector arg
    public Vector3i transform( Vector3i v, Lambda.FuncBinary<Integer,Integer,Integer> binaryFn ) {
        int x = binaryFn.run( this.x, v.x );
        int y = binaryFn.run( this.y, v.y );
        int z = binaryFn.run( this.z, v.z );
        return new Vector3i( x,y,z );
    }
    
    // add two vectors together
    public Vector3i add( Vector3i v ) {
        return this.transform( v, (x,y) -> x + y );
    }

    // add a scalar to each element of a vector
    public Vector3i add( int f ) {
        return this.transform( x -> x + f );
    }

    // negate each element
    public Vector3i negate() {
        return this.transform( (x) -> - x );
    }
    
    // divide each element by a scalar val
    public Vector3i divide( int dsor ) {
        return this.transform( x -> x / dsor );
    }
    
    // multiply each element by a scalar val
    public Vector3i multiply( int mult ) {
        return this.transform( x -> x * mult );
    }
    
    // modulo each element by a scalar val
    public Vector3i modulo( int mod ) {
        return this.transform( x -> x % mod );
    }

    // return the maximum element
    public int max() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public int min() {
        return Math.min( Math.min( this.x, this.y ), this.z );
    }

    @Override
    public int hashCode() {
        this.hashCoder.reset();
        this.hashCoder.addHash( this.x );
        this.hashCoder.addHash( this.y );
        this.hashCoder.addHash( this.z );
        return this.hashCoder.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Vector3i other = (Vector3i) obj;
        return x == other.x && y == other.y && z == other.z;
    }
    
    
}
