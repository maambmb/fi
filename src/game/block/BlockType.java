package game.block;

import util.Vector3in;

// the type of block fully identifies what block it is:
public enum BlockType {

    AIR( 
        BlockClass.ETHER,
        LightSource.CONSTANT,
        Vector3in.ZERO
    ),
    GRAVEL( 
        BlockClass.SOLID,
        LightSource.CONSTANT,
        Vector3in.ZERO
    ),
    GLOWSTONE(
        BlockClass.SOLID,
        LightSource.CONSTANT,
        new Vector3in(255,255,255)
    );
    // the "class" of a block. Block classes have different behaviour with respect to:
    // 1) can they let light through
    // 2) can they can be stood on
    // 3) how they are rendered (like a normal cube or as a cross of 2 quads)
    public BlockClass blockClass;

    // describes the material's light emissions
    public LightSource lightSource;
    public Vector3in illumination;

    private BlockType( BlockClass material, LightSource src, Vector3in illumination ) {
        this.blockClass = material;
        this.lightSource = src;
        this.illumination = illumination;
    }
    
}
