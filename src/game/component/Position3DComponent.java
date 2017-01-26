package game.component;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import game.Entity;
import util.MatrixUtils;
import util.Vector3fl;

public class Position3DComponent implements Component {

    public Vector3fl position;
    public Vector3fl rotation;
    public Matrix4f matrixBuffer;
    public float scale;

    public Position3DComponent() { 
        this( Vector3fl.ZERO );
    }

    public Position3DComponent( Vector3fl pos ) {
        this.position     = pos;
        this.rotation     = Vector3fl.ZERO;
        this.matrixBuffer = new Matrix4f();
        this.scale        = 1f;
    }

    // get the direction the entity is pointed by looking at the rotation vector
    // calculate this by setting up a rotation matrix and applying it to a unit vector
    public Vector3fl getDirectionVector() {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.rotateMatrix( this.matrixBuffer, this.rotation );

        // TODO: fix this...
        Vector4f dirVec =  new Vector4f( 1, 0, 0, 0 );
        Matrix4f.transform( this.matrixBuffer, dirVec, dirVec );
        return new Vector3fl( dirVec );
    }

    // get the direction the entity will march (i.e. move forward)
    // this is trivially the direction vector but with y capped to 0
    // to prevent upward/downward motion
    public Vector3fl getMarchVector() {
        Vector3fl dirVec = this.getDirectionVector();
        dirVec.y = 0;
        return dirVec.normalize();
    }

    // get the direction the entity will strafe (i.e. move laterally)
    // this is calcualted by getting the march vector, and rotating it 90 deg
    // over the Y axis using a rotation matrix
    public Vector3fl getStrafeVector() {
        Vector4f strafeTemp = this.getMarchVector().toVector4f();
        Matrix4f.setIdentity( this.matrixBuffer );
        Matrix4f.rotate( 90, MatrixUtils.Y_ROTATOR, this.matrixBuffer, this.matrixBuffer );
        Matrix4f.transform( this.matrixBuffer, strafeTemp, strafeTemp );
        return new Vector3fl( strafeTemp );
    }

    @Override
    public void setup( Entity e ) {
    }

    @Override
    public void destroy() {
    }

}
