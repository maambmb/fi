package game.block;

public class Occlusion {

	public boolean particleOcclusion;
	public boolean lightOcclusion;
	
	public void reset() {
		this.particleOcclusion = this.lightOcclusion = false;
	}
	
	public void set( Occlusion o ) {
		this.particleOcclusion = o.particleOcclusion;
		this.lightOcclusion = o.lightOcclusion;
	}
	
	public Occlusion( Occlusion o ) {
		this.set( o );
	}
}
