package util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixUtils {

    public final static Vector3f X_ROTATOR = new Vector3f( 1, 0, 0 );
    public final static Vector3f Y_ROTATOR = new Vector3f( 0, 1, 0 );
    public final static Vector3f Z_ROTATOR = new Vector3f( 0, 0, 1 );

    public static void translateMatrix( Matrix4f m, Vector3fl translation ) {
        Matrix4f.translate( translation.toVector3f(), m, m);
    }

    public static void scaleMatrix( Matrix4f m, float scale ) {
        Matrix4f.scale( new Vector3f( scale, scale, scale ), m, m );
    }

    public static void rotateMatrix( Matrix4f m, Vector3fl rotation ) {
        Matrix4f.rotate( (float)Math.toRadians( rotation.x ), X_ROTATOR, m, m );
        Matrix4f.rotate( (float)Math.toRadians( rotation.y ), Y_ROTATOR, m, m );
        Matrix4f.rotate( (float)Math.toRadians( rotation.z ), Z_ROTATOR, m, m );
    }

}
