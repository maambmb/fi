package util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Matrix4fl {

    private static final Vector3f PITCH_AXIS = new Vector3f( 1, 0, 0 );
    private static final Vector3f YAW_AXIS = new Vector3f( 0, 1, 0 );

    public Matrix4f raw;
    
    public Matrix4fl() {
    	this.raw = new Matrix4f();
    	this.clearMatrix();
    }
    
    public void addTranslationToMatrix( Vector3fl v ) {
    	Vector3f vec3 = v.toVector3f();
        Matrix4f.translate( vec3, this.raw, raw );
    }
    
    public void addScaleToMatrix( float scale ) {
        Matrix4f.scale( new Vector3f( scale, scale, scale ), this.raw, this.raw );
    }
    
    public void addScaleToMatrix( Vector3fl scale ) {
        Matrix4f.scale( scale.toVector3f(), this.raw, this.raw );
    }

    public void addPitchToMatrix( float pitch ) {
        Matrix4f.rotate( (float)Math.toRadians( pitch ), PITCH_AXIS, this.raw, this.raw );
    }
    
    public void addYawToMatrix( float yaw ) {
        Matrix4f.rotate( (float)Math.toRadians( yaw ), YAW_AXIS, this.raw, this.raw );
    }
    
    public void clearMatrix() {
    	Matrix4f.setIdentity( this.raw );
    }
    
    public Vector3fl transform( Vector3fl v ) {
		Vector4f vec4 = v.toVector4f();
        Matrix4f.transform( this.raw, vec4, vec4 );
        return new Vector3fl( vec4 );
    }
    

    

}
