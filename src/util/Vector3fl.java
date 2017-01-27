package util;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

// an immutable 3D floateger vector class
public class Vector3fl {

    public static void set( Vector3fl tgt, Vector3fl other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
    }

    public static void set( Vector3fl tgt, Vector4f other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
    }

    public static void set( Vector3fl tgt, Vector3f other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
    }

    public static void set( Vector3f tgt, Vector3fl other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
    }

    public static void set( Vector4f tgt, Vector3fl other ) {
        tgt.x = other.x;
        tgt.y = other.y;
        tgt.z = other.z;
        tgt.w = 0;
    }

    public static void set( Vector3fl tgt, Vector3in other ) {
        tgt.x = (float)other.x;
        tgt.y = (float)other.y;
        tgt.z = (float)other.z;
    }

    public static void set( Vector3fl tgt, float x, float y, float z ) {
        tgt.x = x;
        tgt.y = y;
        tgt.z = z;
    }

    public static void add( Vector3fl tgt, Vector3fl src, Vector3fl other ) {
        tgt.x = src.x + other.x;
        tgt.y = src.y + other.y;
        tgt.z = src.z + other.z;
    }

    public static void add( Vector3fl tgt, Vector3fl src, Vector3in other ) {
        tgt.x = src.x + other.x;
        tgt.y = src.y + other.y;
        tgt.z = src.z + other.z;
    }

    public static void add( Vector3fl tgt, Vector3fl src, float f ) {
        tgt.x = src.x + f;
        tgt.y = src.y + f;
        tgt.z = src.z + f;
    }

    public static void negate( Vector3fl tgt, Vector3fl src ) {
        tgt.x = -src.x;
        tgt.y = -src.y;
        tgt.z = -src.z;
    }

    public static void multiply( Vector3fl tgt, Vector3fl src, float m ) {
        tgt.x = src.x * m;
        tgt.y = src.y * m;
        tgt.z = src.z * m;
    }

    public static void divide( Vector3fl tgt, Vector3fl src, float d ) {
        tgt.x = src.x / d;
        tgt.y = src.y / d;
        tgt.z = src.z / d;
    }

    public static void abs( Vector3fl tgt, Vector3fl src ) {
        tgt.x = Math.abs( src.x );
        tgt.y = Math.abs( src.y );
        tgt.z = Math.abs( src.z );
    }

    public static void max( Vector3f tgt, Vector3f src, float m ) {
        tgt.x = Math.max( src.x, m );
        tgt.y = Math.max( src.y, m );
        tgt.z = Math.max( src.z, m );
    }

    public static void max( Vector3f tgt, Vector3f src, Vector3f other ) {
        tgt.x = Math.max( src.x, other.x );
        tgt.y = Math.max( src.y, other.y );
        tgt.z = Math.max( src.z, other.z );
    }

    public static void min( Vector3f tgt, Vector3f src, int m ) {
        tgt.x = Math.min( src.x, m );
        tgt.y = Math.min( src.y, m );
        tgt.z = Math.min( src.z, m );
    }

    public static void min( Vector3f tgt, Vector3f src, Vector3f other ) {
        tgt.x = Math.min( src.x, other.x );
        tgt.y = Math.min( src.y, other.y );
        tgt.z = Math.min( src.z, other.z );
    }

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

    public Vector3fl( Vector3in v ) {
        set( this, v );
    }

    public Vector3fl( Vector3fl v ) {
        set( this, v );
    }

    public Vector3fl() {
    }

    @Override
    public String toString() {
        return String.format( "Vector3fl( %s, %s, %s )", this.x, this.y, this.z );
    }


}
