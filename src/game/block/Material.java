package game.block;

public enum Material {

    SOLID( true, true ),        
    LIQUID( false, true ),
    CROSS( false, false ),
    TRANSPARENT( true, false ),
    ETHER( false, false );

    // can objects collide with this material
    public boolean solid;

    // does light propagate through this material
    public boolean opaque;

    private Material( boolean solid, boolean opaque ) {
        this.solid = solid;
        this.opaque = opaque;
    }
}

