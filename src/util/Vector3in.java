package util;

// an immutable 3D integer vector class
public class Vector3in {

    // the normals to all 6 faces of a cube
    public enum CubeNormal {

        FRONT( new Vector3in(0,0,-1) ),
        BACK( new Vector3in(0,0,1) ),
        LEFT( new Vector3in(-1,0,1) ),
        RIGHT( new Vector3in(1,0,0) ),
        TOP( new Vector3in(0,-1,0) ),
        BOTTOM( new Vector3in(0,1,0) );

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
                    n.firstOrtho = BOTTOM;
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

    public static final Vector3in ZERO = new Vector3in();
    public static final Vector3in MAX_BYTES = new Vector3in( 0xFFFFFF );

    // the 3 elements of the vector
    public int x;
    public int y;
    public int z;

    // a hashcoder utility object for equality calcs
    private HashCoder hashCoder;

    // most straightforward to create a vector (specify the 3 elements)
    public Vector3in( int x, int y, int z ) {
        this.hashCoder = new HashCoder();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3in() {
        this(0,0,0);
    }

    // specify the elements via a packed integer. Similar to how a hexcode specifies r,g,b values
    public Vector3in( int packed ) {
        this( packed & 0xFF, ( packed >> 8 ) & 0xFF, (packed >> 16 ) & 0xFF );
    }

    public int packBytes() {
        return this.x + ( this.y << 8 ) + (this.z << 16 );
    }

    // add two vectors together
    public Vector3in add( Vector3in v ) {
        return new Vector3in( this.x + v.x, this.y + v.y, this.z + v.z );
    }

    // add a scalar to each element of a vector
    public Vector3in add( int i ) {
        return new Vector3in( this.x + i, this.y + i, this.z + i );
    }

    // negate each element
    public Vector3in negate() {
        return new Vector3in( - this.x, - this.y, - this.z );
    }

    // divide each element by a scalar val
    public Vector3in divide( int dsor ) {
        return new Vector3in( this.x / dsor, this.y / dsor, this.z / dsor );
    }

    // multiply each element by a scalar val
    public Vector3in multiply( int mult ) {
        return new Vector3in( this.x * mult, this.y * mult, this.z * mult );
    }

    // modulo each element by a scalar val
    public Vector3in modulo( int mod ) {
        return new Vector3in( this.x % mod, this.y % mod, this.z % mod );
    }

    // return the maximum element
    public int max() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public int min() {
        return Math.min( Math.min( this.x, this.y ), this.z );
    }

    public Vector3in max( int max ) {
        return new Vector3in( Math.max( this.x, max ), Math.max( this.y, max ), Math.max( this.z, max ) );
    }

    public Vector3in min( int min ) {
        return new Vector3in( Math.min( this.x, min ), Math.min( this.y, min ), Math.min( this.z, min ) );
    }

    public Vector3in max( Vector3in v ) {
        return new Vector3in( Math.max( this.x, v.x ), Math.max( this.y, v.y ), Math.max( this.z, v.z ) );
    }

    public Vector3in min( Vector3in v ) {
        return new Vector3in( Math.min( this.x, v.x ), Math.min( this.y, v.y ), Math.min( this.z, v.z ) );
    }

    public Vector3fl toVector3fl() {
        return new Vector3fl( (float) this.x, (float) this.y, (float) this.z );
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
        Vector3in other = (Vector3in) obj;
        return x == other.x && y == other.y && z == other.z;
    }


}
