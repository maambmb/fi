package game.block;

import util.Vector3in;

// the type of block fully identifies what block it is:
public enum BlockType {

    AIR( 
        false,
        Opacity.INVISIBLE,
        LightSource.CONSTANT,
        Vector3in.ZERO
    ),
    GRAVEL( 
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        Vector3in.ZERO
    ),
    GLOWSTONE(
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        new Vector3in(255,255,255)
    );

    public enum Opacity {
        OPAQUE, TRANSPARENT, CROSSED, INVISIBLE
    }

    public boolean solid;
    public LightSource lightSource;
    public Opacity opacity;
    public Vector3in illumination;

    private BlockType( boolean solid, Opacity opacity, LightSource src, Vector3in illu ) {
        this.solid        = solid;
        this.opacity      = opacity;
        this.lightSource  = src;
        this.illumination = illu;
    }

}
