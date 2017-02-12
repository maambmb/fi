package game.input;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;

import game.Component;
import game.Entity;
import game.Game;
import game.Position3DComponent;
import util.Matrix4fl;
import util.Vector3fl;

public class NoClipComponent implements Component { 

    private static Matrix4fl matrix = new Matrix4fl();

    private Position3DComponent posCmpt;
    private InputListenerComponent inputCmpt;

    public NoClipComponent() {
    }

    @Override
    public void setup( Entity e ) {
        e.listener.addSubscriber( Position3DComponent.class, x -> this.posCmpt = x );
        e.listener.addSubscriber( Game.UpdateMessage.class, this::update );
        e.listener.addSubscriber( InputListenerComponent.class, x -> this.inputCmpt = x );
    }
    
    public void init() {
    	
    }

    private void updatePosition( float delta ) {

        // whether or not to apply strafing or maching this tick (and in which direction)
        // either -1f, 0f or 1f
        float strafe = 0f;
        float march  = 0f;
        
        if( !this.inputCmpt.canListen() )
        	return;

        // TODO: make key bindings configurable
        // determine the strafe/march amounts based on WASD
        if( InputCapturer.GLOBAL.isKeyDown( Key.KEY_D ) )
            strafe = -1f;
        if( InputCapturer.GLOBAL.isKeyDown( Key.KEY_A ) )
            strafe = 1f;
        if( InputCapturer.GLOBAL.isKeyDown( Key.KEY_W ) )
            march = 1f;
        if( InputCapturer.GLOBAL.isKeyDown( Key.KEY_S ) )
            march = -1f;

        // if we're not strafing or marching we're not moving so abort
        if( strafe == 0f && march == 0f)
            return;

        // multiply the march and strafe vecs by their march and strafe amounts,
        // add the vectors together and normalize. Finally we can multiply by the speed.
        // If we naively added march + strafe, you would move faster diagonally (thx pythagorus)
        matrix.clearMatrix();
        matrix.addYawToMatrix( this.posCmpt.rotation.y );
        matrix.addPitchToMatrix( this.posCmpt.rotation.x );
        Vector3fl marchVec = matrix.transform( new Vector3fl(0,0,-1));
        Vector3fl strafeVec = matrix.transform( new Vector3fl(-1,0,0));
        Vector3fl finalVec = strafeVec.multiply( strafe ).add( marchVec.multiply( march ) ).multiply( 0.5f );

        
        this.posCmpt.position = this.posCmpt.position.add( finalVec );
    }

    private void updateRotation( float delta ) {

        int dX = - Mouse.getDX();
        int dY = Mouse.getDY();

        if( dX == 0 && dY == 0 )
            return;
        
        this.posCmpt.rotation.y += dX * 2.65f * delta;
        this.posCmpt.rotation.x += dY * 2.65f * delta;
        this.posCmpt.rotation.x = Math.max( this.posCmpt.rotation.x, -80f );
        this.posCmpt.rotation.x = Math.min( this.posCmpt.rotation.x, 80f );

    }
    

    private void update( Game.UpdateMessage msg ) {

        // get the amount of time elapsed since last tick in seconds
        float delta  = msg.deltaMs / 1000f;
        this.updateRotation( delta );
        this.updatePosition( delta );

    }

    @Override
    public void destroy() {
    }

}
