package game.block;

import game.Config;
import util.Vector3in;

// representing a block/cube of terrain
public class Block {

    // the type of the block
    public BlockType blockType;
    // the lighting within/of the block
    public int[] illumination;
    // whether or not the block is globally lit
    public boolean globalLighting;

    public Block() {
        this.blockType = BlockType.AIR;
        this.illumination = new int[ LightSource.values().length ];
        this.globalLighting = false;
    }

    // reset the illumination by setting it to the natural illumination
    // of the block type. This will remove any propagated light
    public void resetIllumination() {
        for( LightSource src : LightSource.values() )
            this.setIllumination( src, Vector3in.ZERO );
        this.setIllumination( blockType.lightSource, blockType.illumination );
    }

    public Vector3in getIllumination( LightSource src ) {
        return new Vector3in( this.illumination[ src.ordinal() ] );
    }

    private void setIllumination( LightSource src, Vector3in v ) {
        this.illumination[ src.ordinal() ] = v.packBytes();
    }

    // propagate the lighting from a neighboring block onto this block
    // mechanically we take the other block's attenuated light and max it against our own
    // for all light sources
    public void propagate( Block b ) {
        for( LightSource src : LightSource.values() ) {
            Vector3in curr = this.getIllumination( src );
            Vector3in other = b.getIllumination( src ).add( - Config.LIGHT_DROPOFF );
            this.setIllumination( src, curr.max( other ) );
        }
    }

    // a utility method for making the block globally illuminating
    public void addGlobalIllumination() {
        this.setIllumination( LightSource.GLOBAL, Vector3in.MAX_BYTES );
    }

    // a utility method to check if the block is radiating any light at all
    // useful in reducing the amount of wasted propagations during light calcs
    public boolean isLit() {
        for( int x : illumination)
            if( x > 0)
                return true;
        return false;
    }

}
