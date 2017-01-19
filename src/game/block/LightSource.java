package game.block;

public enum LightSource {

    GLOBAL(),
    CONSTANT_SHORT(4),
    CONSTANT();

    public int dropOff;

    private LightSource( int dropOff ) {
        this.dropOff = 0xF * dropOff;
    }

    private LightSource() {
        this( 1 );
    }

}

