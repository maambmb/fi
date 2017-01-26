package game.component;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import game.Entity;
import game.Game;
import util.MatrixUtils;
import util.Vector3fl;

public class Position3DComponent implements Component {

    public static class ModifyMessage {

        public Vector3fl position;
        public Vector3fl rotation;
        public Float scale;
        public boolean abs;

        private ModifyMessage( boolean abs, Vector3fl pos, Vector3fl rotation, Float scale ) {
            this.abs = abs;
            this.position = pos;
            this.rotation = rotation;
            this.scale = scale;
        } 

        public static ModifyMessage setPosition( Vector3fl pos, Vector3fl rotation, Float scale ) {
            return new ModifyMessage( true, pos, rotation, scale );
        }

        public static ModifyMessage adjustPosition( Vector3fl pos, Vector3fl rotation, Float scale ) {
            return new ModifyMessage( false, pos, rotation, scale );
        }
    }


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

    private void update( Game.UpdateMessage msg ) { }

    private void modifyPosition( ModifyMessage msg ) {
        if( msg.position != null )
            this.position = msg.abs ? msg.position : this.position.add( msg.position );
        if( msg.rotation != null )
            this.rotation = msg.abs ? msg.rotation : this.rotation.add( msg.rotation );
        if( msg.scale != null )
            this.scale = msg.abs ? msg.scale : msg.scale + this.scale;
    }

    public Vector3fl getDirectionVector() {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.rotateMatrix( this.matrixBuffer, this.rotation );
        Vector4f dirVec =  new Vector4f( 1, 0, 0, 0 );
        Matrix4f.transform( this.matrixBuffer, dirVec, dirVec );
        return new Vector3fl( dirVec );
    }

    public Vector3fl getMarchVector() {
        Vector3fl dirVec = this.getDirectionVector();
        dirVec.y = 0;
        return dirVec.normalize();
    }

    public Vector3fl getStrafeVector() {
        Matrix4f.setIdentity( this.matrixBuffer );
        Matrix4f.rotate( 90, MatrixUtils.Y_ROTATOR, this.matrixBuffer, this.matrixBuffer );
        Vector4f strafeTemp = this.getMarchVector().toVector4f();
        Matrix4f.transform( this.matrixBuffer, strafeTemp, strafeTemp );
        return new Vector3fl( strafeTemp );
    }

    @Override
    public void setup( Entity e ) {
        e.globalListenerClient.addListener( Game.UpdateMessage.class, this::update );
        e.listener.addListener( ModifyMessage.class, this::modifyPosition );
    }

    @Override
    public void destroy() {
    }

}
