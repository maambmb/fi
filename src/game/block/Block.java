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

    private static Vector3in resetBuffer = new Vector3in();

    public void resetIllumination() {
        Vector3in.zero( resetBuffer );
        for( LightSource src : LightSource.values() )
            this.setIllumination( src, resetBuffer );
        this.setIllumination( blockType.lightSource, blockType.illumination );
    }

    public void getIllumination( LightSource src, Vector3in tgt ) {
        Vector3in.unpackBytes( tgt, this.illumination[ src.ordinal() ] );
    }

    private void setIllumination( LightSource src, Vector3in v ) {
        this.illumination[ src.ordinal() ] = v.packBytes();
    }

    // propagate the lighting from a neighboring block onto this block
    // mechanically we take the other block's attenuated light and max it against our own
    // for all light sources

    private static Vector3in propagateBuffer1 = new Vector3in();
    private static Vector3in propagateBuffer2 = new Vector3in();

    public void propagate( Block b ) {
        for( LightSource src : LightSource.values() ) {
        
            b.getIllumination( src, propagateBuffer1 );
            Vector3in.add( propagateBuffer1, propagateBuffer1, - Config.LIGHT_DROPOFF );

            this.getIllumination( src, propagateBuffer2 );
            Vector3in.max( propagateBuffer1 , propagateBuffer1, propagateBuffer2 );
            this.setIllumination( src, propagateBuffer1 );
        }
    }

    // a utility method for making the block globally illuminating

    private static Vector3in addBuffer = new Vector3in();

    public void addGlobalIllumination() {
        Vector3in.unpackBytes( addBuffer, 0xFFFFFF );
        this.setIllumination( LightSource.GLOBAL, addBuffer );
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
