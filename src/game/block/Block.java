package game.block;

import util.Vector2i;

public enum Block {

    Gravel( new Vector2i( 0, 0 ), Light.NULL, Type.OPAQUE ),
    Glass( new Vector2i( 1, 0 ), Light.NULL, Type.TRANSPARENT );
    
    public enum Type {
        OPAQUE( true ),          // a solid block
        TRANSPARENT( false ),    // a solid transparent block
        CROSS( false ),          // a ethereal block that consists of a cross made by 2 textured quads
        LIQUID( true );          // a liquid block

        public boolean opaque;
        private Type( boolean opaque ) {
            this.opaque = opaque;
        }
    }

    public Vector2i texCoords;
    public Light light;
    public Type blockType;

    // A block consists of a texture, light source and block type
    private Block( Vector2i texCoords, Light light, Type blockType ) {
        this.texCoords = texCoords;
        this.light = light;
        this.blockType = blockType;
    }
    
}
