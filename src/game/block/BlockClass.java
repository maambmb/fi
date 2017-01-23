package game.block;

public enum BlockClass {

    SOLID( true, true ),        
    LIQUID( false, true ),
    TRANSPARENT( true, false ),
    ETHER( false, false );

    // can objects collide with this material
    public boolean solid;

    // does light propagate through this material
    public boolean opaque;

    private BlockClass( boolean solid, boolean opaque ) {
        this.solid = solid;
        this.opaque = opaque;
    }
}

