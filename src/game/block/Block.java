package game.block;

// representing a block/cube of terrain
public class Block {

    // the type of the block
    public BlockType blockType;
    // the lighting within/of the block
    public Illumination illumination;
    // whether or not the block is globally lit
    public boolean globalLighting;

    public Block() {
        this.blockType = BlockType.AIR;
        this.illumination = new Illumination();
        this.globalLighting = false;
    }

}
