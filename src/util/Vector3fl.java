package util;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

// an immutable 3D integer vector class
public class Vector3fl {
	
	public static Vector3fl ONE = new Vector3fl(1,1,1);
	
    // the 3 elements of the vector
    public float x;
    public float y;
    public float z;

    public Vector3fl( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    
    public Vector3fl add( float f ) {
    	return new Vector3fl( this.x + f, this.y + f, this.z + f );
    }
    
    public Vector3fl subtract( Vector3fl v ) {
    	return new Vector3fl( this.x - v.x, this.y - v.y, this.z - v.z );
    }
    
    public Vector3fl multiply( float mult ) {
    	return new Vector3fl( this.x * mult, this.y * mult, this.z * mult );
    }
    
    public Vector3fl multiply( Vector3fl other ) {
    	return new Vector3fl( this.x * other.x, this.y * other.y, this.z * other.z );
    }

    public Vector3fl multiply( Vector3in other ) {
    	return new Vector3fl( this.x * other.x, this.y * other.y, this.z * other.z );
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

    public Vector3fl round() {
    	return new Vector3fl( (float)Math.round( this.x ), (float)Math.round(this.y), (float)Math.round(this.z) );
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
    	return new Vector4f( this.x, this.y, this.z, 1 );
    }

    public Vector3in toVector3in() {
    	return new Vector3in( (int)this.x, (int)this.y, (int)this.z );
    }
    
    public Vector3in toRoundedVector3in() {
    	return new Vector3in( Math.round( this.x ), Math.round(this.y), Math.round(this.z) );
    }
    
    @Override
    public int hashCode() {
    	return HashUtils.hash(this.x,this.y,this.z);
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
        return String.format( "Vector3fl( %.2f, %.2f, %.2f )", this.x, this.y, this.z );
    }


}
