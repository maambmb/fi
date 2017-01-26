package game.component;

import org.lwjgl.input.Keyboard;

import game.Entity;
import game.Game;

import util.Vector3fl;

public class NoClipComponent implements Component { 

    private float moveSpeed;
    private Position3DComponent posCmpt;

    public NoClipComponent( float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void setup( Entity e ) {
        // this component is updating every tick
        e.globalListenerClient.addSubscriber( Game.UpdateMessage.class, this::update );
        
        // this component reads and modifies the entities 3D position
        e.listener.addSubcriber( Position3DComponent.class, x -> this.posCmpt = x );
    }

    private void update( Game.UpdateMessage msg ) {

        // get the amount of time elapsed since last tick in seconds
        float delta  = msg.deltaMs / 1000f;

        // whether or not to apply strafing or maching this tick (and in which direction)
        // either -1f, 0f or 1f
        float strafe = 0f;
        float march  = 0f;

        // TODO: make key bindings configurable
        // determine the strafe/march amounts based on WASD
        if( Keyboard.isKeyDown( Keyboard.KEY_D ))
            strafe = 1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_A ))
            strafe = -1f;
        if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
            march = 1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
            march = -1f;

        // if we're not strafing or marching we're not moving so abort
        if( strafe == 0f && march == 0f)
            return;

        // get the vector pointing in the marching direction 
        Vector3fl marchVec = this.posCmpt.getMarchVector().multiply( strafe );
        
        // get the vector pointing in the strafing direction
        Vector3fl strafeVec = this.posCmpt.getStrafeVector().multiply( march );

        // multiply the march and strafe vecs by their march and strafe amounts,
        // add the vectors together and normalize. Finally we can multiply by the speed.
        // If we naively added march + strafe, you would move faster diagonally (thx pythagorus)
        this.posCmpt.position = marchVec.add( strafeVec ).multiply( delta * this.moveSpeed );
    }

    @Override
    public void destroy() {
    }

}
