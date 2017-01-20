package game.block;

import game.Config;

// the various light sources in the game
// each light source can have multiple lights, but
// they share a common modulator/multiplier
public enum LightSource {

    GLOBAL(),
    CONSTANT_SHORT(4),
    CONSTANT();

    public int dropOff;

    // specify a drop-off, which multiplies the rate at which light attenuates as it travels
    private LightSource( int dropOff ) {
        this.dropOff = ( 256 / Config.LIGHT_JUMPS ) * dropOff;
    }

    // default drop off multiplier rate is obviously 1 (no change)
    private LightSource() {
        this( 1 );
    }

}

