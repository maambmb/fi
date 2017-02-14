package util;

// an immutable 3D integer vector class
public class Vector3in {

    // the normals to all 6 faces of a cube
    public enum CubeNormal {

        FRONT( new Vector3in(0,0,1) ),
        BACK( new Vector3in(0,0,-1) ),
        LEFT( new Vector3in(-1,0,0) ),
        RIGHT( new Vector3in(1,0,0) ),
        TOP( new Vector3in(0,1,0) ),
        BOTTOM( new Vector3in(0,-1,0) );

        // the underlying normal unit vector
        public Vector3in vector; 

        private CubeNormal( Vector3in v ) {
            this.vector = v;
        }
    }
    
    public static Vector3in ZERO = new Vector3in();
    
    public static Vector3in WHITE = new Vector3in(0xFFFFFF);
    public static Vector3in GREEN = new Vector3in(0x00FF00);
    public static Vector3in RED = new Vector3in(0x0000FF);
    public static Vector3in BLUE = new Vector3in(0xFF0000);
    public static Vector3in MAGENTA = new Vector3in(0xFF00FF);
    public static Vector3in YELLOW = new Vector3in(0x00FFFF);
    public static Vector3in CYAN = new Vector3in(0xFFFF00);
    
    // the 3 elements of the vector
    public int x;
    public int y;
    public int z;

    public Vector3in( int x, int y, int z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3in( int x, int y) {
    	this( x,y,0);
    }

    public Vector3in() {
    }

    public Vector3in( int packed ) {
    	this( ( packed >> 16 ) & 0xFF, ( packed >> 8 ) & 0xFF, packed  & 0xFF );
    }
    
    public Vector3in add( Vector3in v ) {
    	return new Vector3in( this.x + v.x, this.y + v.y, this.z + v.z );
    }
    
    public Vector3in add( int i ) {
    	return new Vector3in( this.x + i, this.y + i, this.z + i );
    }
    
    public Vector3in subtract( Vector3in v ) {
    	return new Vector3in( this.x - v.x, this.y - v.y, this.z - v.z );
    }
    
    public Vector3in multiply( int mult ) {
    	return new Vector3in( this.x * mult, this.y * mult, this.z * mult );
    }
    
    public Vector3in divide( int dsor ) {
    	return new Vector3in( this.x / dsor, this.y / dsor, this.z / dsor );
    }

    public Vector3in partition( int dsor ) {
    	Vector3in mod = this.modulo( dsor );
    	Vector3in div = this.divide( dsor );
    	if( this.x < 0 && mod.x != 0 )
    		div.x -= 1;
    	if( this.y < 0 && mod.y != 0 )
    		div.y -= 1;
    	if( this.z < 0 && mod.z != 0 )
    		div.z -= 1;
    	return div;
    }
    
    public Vector3in modulo( int mod ) {
    	return new Vector3in( this.x % mod, this.y % mod, this.z % mod );
    }
    
    public Vector3in abs() {
    	return new Vector3in( Math.abs( this.x ), Math.abs( this.y ), Math.abs( this.z ) );
    }
    
    public Vector3in max( Vector3in v ) {
    	return new Vector3in( Math.max( this.x, v.x ), Math.max( this.y, v.y ), Math.max( this.z, v.z ));
    }

    public Vector3in max( int max ) {
    	return new Vector3in( Math.max( this.x, max ), Math.max( this.y, max ), Math.max( this.z, max ));
    }
    
    public Vector3in min( Vector3in v ) {
    	return new Vector3in( Math.min( this.x, v.x ), Math.min( this.y, v.y ), Math.min( this.z, v.z ));
    }

    public Vector3in min( int min ) {
    	return new Vector3in( Math.min( this.x, min ), Math.min( this.y, min ), Math.min( this.z, min ));
    }

    // return the maximum element
    public int toMaxElement() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public int toMinElement() {
        return Math.min( Math.min( this.x, this.y ), this.z );
    }

    public int toPackedBytes() {
        return ( this.x << 16 ) + ( this.y << 8 ) + this.z;
    }
    
    public Vector3fl toVector3fl() {
    	return new Vector3fl( this.x, this.y, this.z );
    }
    
    @Override
    public int hashCode() {
        return HashUtils.hash( this.x, this.y, this.z );
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
