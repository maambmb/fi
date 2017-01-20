package game.block;

import util.Vector3i;

// the type of block fully identifies what block it is:
public enum BlockType {

    AIR( 
        null,
        BlockClass.ETHER,
        new Illumination()
    ),
    GRAVEL( 
        new Vector3i( 1,0,0 ), 
        BlockClass.SOLID,
        new Illumination()
    ),
    GLOWSTONE(
        new Vector3i( 2,0,0 ), 
        BlockClass.SOLID,
        new Illumination( LightSource.CONSTANT_SHORT, new Vector3i( 0xFFFFFF ) )
    );

    // 2D coordinates of texture on the texture atlas
    public Vector3i texCoords;

    // the "class" of a block. Block classes have different behaviour with respect to:
    // 1) can they let light through
    // 2) can they can be stood on
    // 3) how they are rendered (like a normal cube or as a cross of 2 quads)
    public BlockClass blockClass;

    // describes the material's light emissions
    public Illumination illumination;

    private BlockType( Vector3i texCoords, BlockClass material, Illumination illumination ) {
        this.texCoords = texCoords;
        this.blockClass = material;
        this.illumination = illumination;
    }
    
}
