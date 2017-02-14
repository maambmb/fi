package game.block;

import util.Vector3in;

// the type of block fully identifies what block it is:
public enum Block {

    AIR( 
        false,
        Opacity.INVISIBLE,
        LightSource.CONSTANT,
        new Vector3in(),
        null
    ),
    LIT_AIR( 
        false,
        Opacity.INVISIBLE,
        LightSource.GLOBAL,
        Vector3in.WHITE,
        null
    ),
    WAVY_PURPLE( 
        true,
        Opacity.OPAQUE,
        LightSource.CONSTANT,
        new Vector3in(),
        new Vector3in(0,0,0)
    ),
    STRIPED_GREEN(
    	true,
    	Opacity.OPAQUE,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(1,0,0)
    ),
    DOTTED_BROWN(
    	true,
    	Opacity.OPAQUE,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(0,1,0)
    ),
    DASHED_SAND(
    	true,
    	Opacity.OPAQUE,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(0,2,0)
    ),
    WAVY_BROWN(
    	true,
    	Opacity.OPAQUE,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(1,2,0)
    ),
    DOTTED_CYAN(
    	true,
    	Opacity.OPAQUE,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(1,1,0)
    ),
    LUSH_SHRUBS_1(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(2,2,0)
    ),
    LUSH_SHRUBS_2(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(3,2,0)
    ),
    LUSH_SHRUBS_3(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(),
    	new Vector3in(4,2,0)
    ),
    GREEN_GLOWSHROOM(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(0x9cff3b),
    	new Vector3in(0,3,0)
    ),
    BLUE_GLOWSHROOM(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(0x00c6ff),
    	new Vector3in(0,3,0)
    ),
    RED_GLOWSHROOM(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(0xf68585),
    	new Vector3in(0,3,0)
    ),
    PURPLE_GLOWSHROOM(
    	false,
    	Opacity.CROSSED,
    	LightSource.CONSTANT,
    	new Vector3in(0xf685e7),
    	new Vector3in(0,3,0)
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

    private Block( boolean solid, Opacity opacity, LightSource src, Vector3in illu, Vector3in texCoords ) {
        this.solid        = solid;
        this.opacity      = opacity;
        this.lightSource  = src;
        this.illumination = illu;
        this.texCoords    = texCoords;
    }

}
