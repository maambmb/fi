package util;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

// an immutable 3D float vector class
public class Vector3fl {

    public static final Vector3fl ZERO = new Vector3fl();

    // the 3 elements of the vector
    public float x;
    public float y;
    public float z;

    // most straightforward to create a vector (specify the 3 elements)
    public Vector3fl( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3fl( Vector3f v ) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3fl( Vector4f v ) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public Vector3fl() {
        this(0,0,0);
    }

    public Vector3fl normalize() {
        float sqrt = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z );
        return new Vector3fl( this.x / sqrt, this.y / sqrt, this.z / sqrt );        
    }

    // add two vectors together
    public Vector3fl add( Vector3fl v ) {
        return new Vector3fl( this.x + v.x, this.y + v.y, this.z + v.z );
    }

    // add a scalar to each element of a vector
    public Vector3fl add( float i ) {
        return new Vector3fl( this.x + i, this.y + i, this.z + i );
    }

    // negate each element
    public Vector3fl negate() {
        return new Vector3fl( - this.x, - this.y, - this.z );
    }

    // divide each element by a scalar val
    public Vector3fl divide( float dsor ) {
        return new Vector3fl( this.x / dsor, this.y / dsor, this.z / dsor );
    }

    // multiply each element by a scalar val
    public Vector3fl multiply( float mult ) {
        return new Vector3fl( this.x * mult, this.y * mult, this.z * mult );
    }

    // modulo each element by a scalar val
    public Vector3fl modulo( float mod ) {
        return new Vector3fl( this.x % mod, this.y % mod, this.z % mod );
    }

    // return the maximum element
    public float max() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public float min() {
        return Math.min( Math.min( this.x, this.y ), this.z );
    }

    public Vector3fl max( float max ) {
        return new Vector3fl( Math.max( this.x, max ), Math.max( this.y, max ), Math.max( this.z, max ) );
    }

    public Vector3fl min( float min ) {
        return new Vector3fl( Math.min( this.x, min ), Math.min( this.y, min ), Math.min( this.z, min ) );
    }

    public Vector3fl max( Vector3fl v ) {
        return new Vector3fl( Math.max( this.x, v.x ), Math.max( this.y, v.y ), Math.max( this.z, v.z ) );
    }

    public Vector3fl min( Vector3fl v ) {
        return new Vector3fl( Math.min( this.x, v.x ), Math.min( this.y, v.y ), Math.min( this.z, v.z ) );
    }

    public Vector3f toVector3f() {
        return new Vector3f( this.x, this.y, this.z );
    }

    public Vector4f toVector4f() {
        return new Vector4f( this.x, this.y, this.z, 0 );
    }


}
