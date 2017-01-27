package game.component;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import game.Entity;
import game.Game;

import util.Vector3fl;

public class NoClipComponent implements Component { 

    private Position3DComponent posCmpt;

    public NoClipComponent() {
    }

    @Override
    public void setup( Entity e ) {
        // this component is updating every tick
        e.globalListenerClient.addSubscriber( Game.UpdateMessage.class, this::update );
        
        // this component reads and modifies the entities 3D position
        e.listener.addSubcriber( Position3DComponent.class, x -> this.posCmpt = x );
    }

    private static Vector3fl marchBuffer = new Vector3fl();

    private void updatePosition( float delta ) {

        // whether or not to apply strafing or maching this tick (and in which direction)
        // either -1f, 0f or 1f
        float strafe = 0f;
        float march  = 0f;

        // TODO: make key bindings configurable
        // determine the strafe/march amounts based on WASD
        if( Keyboard.isKeyDown( Keyboard.KEY_D ))
            strafe = 1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_A ))
            strafe = 1f;
        if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
            march = -1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
            march = 1f;

        // if we're not strafing or marching we're not moving so abort
        if( strafe == 0f && march == 0f)
            return;

        // get the vector pointing in the marching direction 
        this.posCmpt.getDirectionVector( marchBuffer );
        Vector3fl.multiply( marchBuffer, marchBuffer, march );

        // multiply the march and strafe vecs by their march and strafe amounts,
        // add the vectors together and normalize. Finally we can multiply by the speed.
        // If we naively added march + strafe, you would move faster diagonally (thx pythagorus)
        Vector3fl.add( this.posCmpt.position, this.posCmpt.position, marchBuffer );
        System.out.println( this.posCmpt.position );
    }

    private void updateRotation( float delta ) {

        int dX = - Mouse.getDX();
        int dY = Mouse.getDY();


        if( dX == 0 && dY == 0 )
            return;

        this.posCmpt.rotation.y += dX * 0.65f * delta;
        float newRot = this.posCmpt.rotation.x + dY * 0.65f * delta;
        this.posCmpt.rotation.x = Math.min( Math.max( -70, newRot ), 70 );
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
