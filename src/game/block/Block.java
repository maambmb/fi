package game.block;

public class Block {

    public BlockType blockType;
    public Illumination chunkIllumination;
    public Illumination illumination;

    public Block() {
        this.blockType = BlockType.AIR;
        this.chunkIllumination = new Illumination();
        this.illumination = new Illumination();
    }

}
