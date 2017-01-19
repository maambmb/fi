package game.block;

import util.Vector2i;
import util.Vector3i;

public enum BlockType {

    AIR( 
        null,
        Material.ETHER,
        new Illumination()
    ),
    GRAVEL( 
        new Vector2i( 1,0 ), 
        Material.SOLID,
        new Illumination()
    ),
    GLOWSTONE(
        new Vector2i( 2,0 ), 
        Material.SOLID,
        new Illumination().set( LightSource.CONSTANT_SHORT, new Vector3i( 0xFFFFFF ) )
    );

    // 2D coordinates of texture on the texture atlas
    public Vector2i texCoords;

    // describes both the material's light transmitting and collision properties
    public Material material;

    // describes the material's light emissions
    public Illumination illumination;

    private BlockType( Vector2i texCoords, Material material, Illumination illumination ) {
        this.texCoords = texCoords;
        this.material = material;
        this.illumination = illumination;
    }
    
}
