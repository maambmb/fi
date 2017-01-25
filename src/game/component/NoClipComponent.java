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
        e.globalListenerClient.addListener( Game.UpdateMessage.class, this::update );
        e.listener.addListener( Position3DComponent.class, x -> this.posCmpt = x );
    }

    private void update( Game.UpdateMessage msg ) {

        float delta  = msg.deltaMs / 1000f;
        float strafe = 0f;
        float march  = 0f;

        if( Keyboard.isKeyDown( Keyboard.KEY_D ))
            strafe = 1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_A ))
            strafe = -1f;
        if( Keyboard.isKeyDown( Keyboard.KEY_S ) )
            march = 1f;
        else if( Keyboard.isKeyDown( Keyboard.KEY_W ) )
            march = -1f;

        if( strafe == 0f && march == 0f)
            return;

        Vector3fl marchVec    = this.posCmpt.getMarchVector().multiply( strafe );
        Vector3fl strafeVec   = this.posCmpt.getStrafeVector().multiply( march );
        this.posCmpt.position = marchVec.add( strafeVec ).multiply( delta * this.moveSpeed );
    }

}
