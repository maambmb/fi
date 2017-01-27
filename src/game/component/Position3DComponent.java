package game.component;

import org.lwjgl.util.vector.Matrix4f;

import game.Entity;
import util.MatrixUtils;
import util.Vector3fl;

public class Position3DComponent implements Component {

    public Vector3fl position;
    public Vector3fl rotation;
    public float scale;

    private static Matrix4f matrixBuffer = new Matrix4f();

    public Position3DComponent() { 
        this( new Vector3fl() );
    }

    public Position3DComponent( Vector3fl pos ) {
        this.position     = pos;
        this.rotation     = new Vector3fl(0,0,0);
        this.scale        = 1f;
    }

    // get the direction the entity is pointed by looking at the rotation vector
    // calculate this by setting up a rotation matrix and applying it to a unit vector
    public void getDirectionVector( Vector3fl dirVec ) {
        Matrix4f.setIdentity( matrixBuffer );
        MatrixUtils.rotateMatrix( matrixBuffer, this.rotation );
        Vector3fl.set( dirVec, 0f, 0f, -1f );
        MatrixUtils.applyMatrix( matrixBuffer, dirVec, dirVec );
    }

    public void setup( Entity e ) {
    }

    @Override
    public void destroy() {
    }

}
