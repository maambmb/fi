package game.block;

public enum ChunkState {
	
	DIRECT_BLOCK(0x01),
	OCCLUSION(0x02),
	INDIRECT(0x04),
	REQUISITE(0x08);

	public int flag;
	private ChunkState( int flag ) {
		this.flag = flag;
	}
	
}
