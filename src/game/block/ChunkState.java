package game.block;

public enum ChunkState {
	
	DIRTY_BLOCK(0x01),
	DIRTY_OCCLUSION(0x02),
	DIRTY_NEIGHBOR(0x04),
	REQUISITE(0x08);
	
	public static int DIRECTLY_DIRTY = DIRTY_BLOCK.flag | DIRTY_OCCLUSION.flag;
	public static int DIRTY = DIRTY_BLOCK.flag | DIRTY_OCCLUSION.flag | DIRTY_NEIGHBOR.flag;

	public int flag;
	private ChunkState( int flag ) {
		this.flag = flag;
	}
	
}
