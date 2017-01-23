package util;

// an immutable 3D float vector class
public class Vector3f {

    // the 3 elements of the vector
    public float x;
    public float y;
    public float z;

    // a hashcoder utility object for equality calcs
    private HashCoder hashCoder;
        
    // most straightforward to create a vector (specify the 3 elements)
    public Vector3f( float x, float y, float z ) {
        this.hashCoder = new HashCoder();
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3f() {
    	this( 0, 0, 0 ); 
    }

    // transform each element of the vector by some unary x -> y fn (returns a new vector)
    public Vector3f transform( Lambda.FuncUnary<Float,Float> unaryFn ) {
        float x = unaryFn.run( this.x );
        float y = unaryFn.run( this.y );
        float z = unaryFn.run( this.z );
        return new Vector3f( x,y,z );
    }

    // transform each element of the vector by some binary x,y -> z fn, where the y values come from the other vector arg
    public Vector3f transform( Vector3f v, Lambda.FuncBinary<Float,Float,Float> binaryFn ) {
        float x = binaryFn.run( this.x, v.x );
        float y = binaryFn.run( this.y, v.y );
        float z = binaryFn.run( this.z, v.z );
        return new Vector3f( x,y,z );
    }
    
    // add two vectors together
    public Vector3f add( Vector3f v ) {
        return this.transform( v, (x,y) -> x + y );
    }

    // add a scalar to each element of a vector
    public Vector3f add( float f ) {
        return this.transform( x -> x + f );
    }

    // negate each element
    public Vector3f negate() {
        return this.transform( (x) -> - x );
    }
    
    // divide each element by a scalar val
    public Vector3f divide( float dsor ) {
        return this.transform( x -> x / dsor );
    }
    
    // multiply each element by a scalar val
    public Vector3f multiply( float mult ) {
        return this.transform( x -> x * mult );
    }
    
    // return the maximum element
    public float max() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public float min() {
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
        Vector3f other = (Vector3f) obj;
        return x == other.x && y == other.y && z == other.z;
    }
    
    
}
