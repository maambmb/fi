package util;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

// an immutable 3D integer vector class
public class Vector3fl {

    // the 3 elements of the vector
    public float x;
    public float y;
    public float z;

    public Vector3fl( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3fl( Vector3in v ) {
    	this(v.x,v.y,v.z);
    }
    
    public Vector3fl( float x, float y) {
    	this( x, y, 0f );
    }
    
    public Vector3fl( Vector3f v ) {
    	this(v.x,v.y,v.z);
    }

    public Vector3fl( Vector4f v ) {
    	this(v.x,v.y,v.z);
    }

    public Vector3fl() {
    }
    
    public Vector3fl add( Vector3fl v ) {
    	return new Vector3fl( this.x + v.x, this.y + v.y, this.z + v.z );
    }
    
    public Vector3fl subtract( Vector3fl v ) {
    	return new Vector3fl( this.x - v.x, this.y - v.y, this.z - v.z );
    }
    
    public Vector3fl multiply( float mult ) {
    	return new Vector3fl( this.x * mult, this.y * mult, this.z * mult );
    }
    
    public Vector3fl divide( float dsor ) {
    	return new Vector3fl( this.x / dsor, this.y / dsor, this.z / dsor );
    }
    
    public Vector3fl max( Vector3fl v ) {
    	return new Vector3fl( Math.max( this.x, v.x ), Math.max( this.y, v.y ), Math.max( this.z, v.z ));
    }
    
    public Vector3fl radians() {
    	return new Vector3fl( (float)Math.toRadians( this.x ), (float)Math.toRadians( this.y ), (float)Math.toRadians( this.z ) );
    }

    public Vector3fl max( float max ) {
    	return new Vector3fl( Math.max( this.x, max ), Math.max( this.y, max ), Math.max( this.z, max ));
    }
    
    public Vector3fl min( Vector3fl v ) {
    	return new Vector3fl( Math.min( this.x, v.x ), Math.min( this.y, v.y ), Math.min( this.z, v.z ));
    }

    public Vector3fl min( float min ) {
    	return new Vector3fl( Math.min( this.x, min ), Math.min( this.y, min ), Math.min( this.z, min ));
    }

    // return the maximum element
    public float toMaxElement() {
        return Math.max( Math.max( this.x, this.y ), this.z );
    }

    // return the minimum element
    public float toMinElement() {
        return Math.min( Math.min( this.x, this.y ), this.z );
    }
    
    public Vector3f toVector3f() {
    	return new Vector3f( this.x, this.y, this.z );
    }
    
    public Vector4f toVector4f() {
    	return new Vector4f( this.x, this.y, this.z, 0 );
    }
    
    public Vector3fl transform( Matrix4f m ) {
    	Vector4f vec4 = this.toVector4f();
        Matrix4f.transform( m, vec4, vec4 );
        return new Vector3fl( vec4 );
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
        Vector3fl other = (Vector3fl) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public String toString() {
        return String.format( "Vector3fl( %s, %s, %s )", this.x, this.y, this.z );
    }


}
