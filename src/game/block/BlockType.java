package game.block;

import util.Vector3in;

// the type of block fully identifies what block it is:
public enum BlockType {

    AIR( 
        false,
        Opacity.INVISIBLE,
        LightSource.CONSTANT,
        Vector3in.ZERO,
        null
    ),
    GRAVEL( 
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        Vector3in.ZERO,
        new Vector3in( 0, 0, 0 )
    ),
    GLOWSTONE(
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        new Vector3in(255,255,255),
        new Vector3in( 0, 1, 0 )
    );

    public enum Opacity {
        OPAQUE, TRANSPARENT, CROSSED, INVISIBLE
    }

    public boolean solid;
    public LightSource lightSource;
    public Opacity opacity;
    public Vector3in texCoords;
    public Vector3in illumination;

    private BlockType( boolean solid, Opacity opacity, LightSource src, Vector3in illu, Vector3in texCoords ) {
        this.solid        = solid;
        this.opacity      = opacity;
        this.lightSource  = src;
        this.illumination = illu;
        this.texCoords    = texCoords;
    }

}
