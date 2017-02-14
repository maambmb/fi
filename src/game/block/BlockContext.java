package game.block;

import util.Vector3in;

// representing a block/cube of terrain
public class BlockContext {

    // the type of the block
    public Block block;

    // the lighting within/of the block
    public int[] illumination;

    public BlockContext() {
        this.block = Block.AIR;
        this.illumination = new int[ LightSource.values().length ];
    }

    // reset the illumination by setting it to the natural illumination
    // of the block type. This will remove any propagated light
    public void reset() {
    	for( int i = 0; i < this.illumination.length; i += 1 )
    		this.illumination[i] = 0;
        this.setIllumination( block.lightSource, block.illumination );
    }

    public Vector3in getIllumination( LightSource src ) {
    	return new Vector3in( this.illumination[ src.ordinal() ] );
    }

    public void setIllumination( LightSource src, Vector3in v ) {
        this.illumination[ src.ordinal() ] = v.toPackedBytes();
    }

    // propagate the lighting from a neighboring block onto this block
    // mechanically we take the other block's attenuated light and max it against our own
    // for all light sources
    public void propagate( BlockContext b, int drop ) {
        for( LightSource src : LightSource.values() ) {
        	Vector3in otherIllu = b.getIllumination( src ).add( - drop );
        	Vector3in thisIllu = this.getIllumination( src );
        	this.setIllumination( src, thisIllu.max( otherIllu ) );
        }
    }
    
    // a utility method to check if the block is radiating any light at all
    // useful in reducing the amount of wasted propagations during light calcs
    public boolean isLit() {
        for( int x : illumination)
            if( x > 0)
                return true;
        return false;
    }
    
    public void recalculateOcclusion() {
    	//...
    }

}
