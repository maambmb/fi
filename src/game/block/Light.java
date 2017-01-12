package game.block;

import util.Vector3i;

public enum Light {

    NULL( Vector3i.ZERO ),
    GLOBAL( new Vector3i( 255, 255, 255 ) );

    public Vector3i lightLevel;
    private Light( Vector3i lightLevel ) {
        this.lightLevel = lightLevel;
    }

}
