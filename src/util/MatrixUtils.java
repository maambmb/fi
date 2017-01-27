package util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class MatrixUtils {


    public final static Vector3f X_ROTATOR = new Vector3f( 1, 0, 0 );
    public final static Vector3f Y_ROTATOR = new Vector3f( 0, 1, 0 );
    public final static Vector3f Z_ROTATOR = new Vector3f( 0, 0, 1 );

    private static Vector3f vec3fBuffer = new Vector3f();
    private static Vector4f vec4fBuffer = new Vector4f();

    public static void translateMatrix( Matrix4f m, Vector3fl v ) {
        Vector3fl.set( vec3fBuffer, v );
        Matrix4f.translate( vec3fBuffer, m, m);
        Vector3fl.set( v, vec3fBuffer );
    }

    public static void scaleMatrix( Matrix4f m, float scale ) {
        Matrix4f.scale( new Vector3f( scale, scale, scale ), m, m );
    }

    public static void rotateMatrix( Matrix4f m, Vector3fl rotation ) {
        Matrix4f.rotate( (float)Math.toRadians( rotation.x ), X_ROTATOR, m, m );
        Matrix4f.rotate( (float)Math.toRadians( rotation.y ), Y_ROTATOR, m, m );
        Matrix4f.rotate( (float)Math.toRadians( rotation.z ), Z_ROTATOR, m, m );
    }


    public static void applyMatrix( Matrix4f m, Vector3fl src, Vector3fl tgt ) {
        Vector3fl.set( vec4fBuffer, src );
        Matrix4f.transform( m, vec4fBuffer, vec4fBuffer );
        Vector3fl.set( tgt, vec4fBuffer );
    }

}
