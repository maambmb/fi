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
    GLOW_BLOCK( 
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        new Vector3in( 0xFFFFFF ),
        new Vector3in( 0, 1, 0 )
    );

    public enum Opacity {
        //fully opaque - does not allow light to propagate
        OPAQUE, 
        // partially transparent cube
        TRANSPARENT, 
        // partially transparent cross
        // where a cross is formed by 2 quads making a cross intersection
        // as opposed to 6 quads forming a cube
        CROSSED, 
        // a fully invisible block that shouldn't be rendered
        INVISIBLE
    }

    // can the block be walked on or do entities fall through
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
