package game.block;

import util.HashUtils;

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
	
	public Occlusion() {
		
	}

    @Override
    public int hashCode() {
    	return HashUtils.hash( this.particleOcclusion, this.lightOcclusion );
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Occlusion other = (Occlusion) obj;
        return other.particleOcclusion == this.particleOcclusion && other.lightOcclusion == this.lightOcclusion;
    }
}
