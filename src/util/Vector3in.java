package util;

// an immutable 3D integer vector class
public class Vector3in {

    // the normals to all 6 faces of a cube
    public enum CubeNormal {

        FRONT( new Vector3in(0,0,1) ),
        BACK( new Vector3in(0,0,-1) ),
        LEFT( new Vector3in(-1,0,1) ),
        RIGHT( new Vector3in(1,0,0) ),
        TOP( new Vector3in(0,1,0) ),
        BOTTOM( new Vector3in(0,-1,0) );

        // the underlying normal unit vector
        public Vector3in vector; 
        // the two (POSITIVE) orthogonal normals 
        public CubeNormal firstOrtho;
        public CubeNormal secondOrtho;

        // initialize the firstOrtho and secondOrtho fields - can't be done in the constructor
        // as the enum isn't fully defined at that point
        public static void init() {
            for( CubeNormal n : CubeNormal.values() ) {
                if( n == FRONT || n == BACK ) {
                    n.firstOrtho = RIGHT;
                    n.secondOrtho = BOTTOM;
                } else if( n == LEFT || n == RIGHT ) {
                    n.firstOrtho = BACK;
                    n.secondOrtho = BOTTOM;
                } else {
                    n.firstOrtho = RIGHT;
                    n.secondOrtho = BACK;
                }
            }
        }

        private CubeNormal( Vector3in v ) {
            this.vector = v;
        }
    }

    public static void zero( Vector3in tgt ) {
        tgt.x = 0;
        tgt.y = 0;
        tgt.z = 0;
    }

    public static void set( Vector3in tgt, Vector3in other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
    }

    public static void set( Vector3in tgt, int x, int y, int z) {
        tgt.x = x;
        tgt.y = y;
        tgt.z = z;
    }

    public static void add( Vector3in tgt, Vector3in src, Vector3in other ) {
        tgt.x = src.x + other.x;
        tgt.y = src.y + other.y;
        tgt.z = src.z + other.z;
    }

    public static void subtract( Vector3in tgt, Vector3in src, Vector3in other ) {
        tgt.x = src.x - other.x;
        tgt.y = src.y - other.y;
        tgt.z = src.z - other.z;
    }

    public static void add( Vector3in tgt, Vector3in src, int i ) {
        tgt.x = src.x + i;
        tgt.y = src.y + i;
        tgt.z = src.z + i;
    }

    public static void negate( Vector3in tgt, Vector3in src ) {
        tgt.x = -src.x;
        tgt.y = -src.y;
        tgt.z = -src.z;
    }

    public static void multiply( Vector3in tgt, Vector3in src, int m ) {
        tgt.x = src.x * m;
        tgt.y = src.y * m;
        tgt.z = src.z * m;
    }

    public static void divide( Vector3in tgt, Vector3in src, int d ) {
        tgt.x = src.x / d;
        tgt.y = src.y / d;
        tgt.z = src.z / d;
    }

    public static void modulo( Vector3in tgt, Vector3in src, int m ) {
        tgt.x = src.x % m;
        tgt.y = src.y % m;
        tgt.z = src.z % m;
    }

    public static void abs( Vector3in tgt, Vector3in src ) {
        tgt.x = Math.abs( src.x );
        tgt.y = Math.abs( src.y );
        tgt.z = Math.abs( src.z );
    }

    public static void segment( Vector3in tgt, Vector3in src, int d ) {

        boolean decX = src.x < 0 && src.x % d != 0;
        boolean decY = src.y < 0 && src.y % d != 0;
        boolean decZ = src.z < 0 && src.z % d != 0;

        divide( tgt, src, d );

        if( decX )
            tgt.x -= 1;
        if( decY )
            tgt.y -= 1;
        if( decZ ) 
            tgt.z -= 1;
    }

    public static void max( Vector3in tgt, Vector3in src, int m ) {
        tgt.x = Math.max( src.x, m );
        tgt.y = Math.max( src.y, m );
        tgt.z = Math.max( src.z, m );
    }

    public static void max( Vector3in tgt, Vector3in src, Vector3in other ) {
        tgt.x = Math.max( src.x, other.x );
        tgt.y = Math.max( src.y, other.y );
        tgt.z = Math.max( src.z, other.z );
    }

    public static void min( Vector3in tgt, Vector3in src, int m ) {
        tgt.x = Math.min( src.x, m );
        tgt.y = Math.min( src.y, m );
        tgt.z = Math.min( src.z, m );
    }

    public static void min( Vector3in tgt, Vector3in src, Vector3in other ) {
        tgt.x = Math.min( src.x, other.x );
        tgt.y = Math.min( src.y, other.y );
        tgt.z = Math.min( src.z, other.z );
    }

    public static void unpackBytes( Vector3in tgt, int bytes ) {
        tgt.x = bytes & 0xFF;
        tgt.y = ( bytes >> 8 ) & 0xFF;
        tgt.z = ( bytes >> 16 ) & 0xFF;
    }

    // the 3 elements of the vector
    public int x;
    public int y;
    public int z;

    // most straightforward to create a vector (specify the 3 elements)
    public Vector3in( int x, int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3in( Vector3in v ) {
        set( this, v );
    }

    public Vector3in() {
    }

    // specify the elements via a packed integer. Similar to how a hexcode specifies r,g,b values
    public Vector3in( int packed ) {
        unpackBytes( this, packed );
    }

    public int packBytes() {
        return this.x + ( this.y << 8 ) + (this.z << 16 );
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
        HashCoder.HASH_CODER.reset();
        HashCoder.HASH_CODER.addHash( this.x );
        HashCoder.HASH_CODER.addHash( this.y );
        HashCoder.HASH_CODER.addHash( this.z );
        return HashCoder.HASH_CODER.hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Vector3in other = (Vector3in) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public String toString() {
        return String.format( "Vector3in( %s, %s, %s )", this.x, this.y, this.z );
    }


}
