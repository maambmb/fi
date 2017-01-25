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

    public void propagate( Block b ) {
        for( LightSource src : LightSource.values() ) {
            Vector3in curr = this.getIllumination( src );
            Vector3in other = b.getIllumination( src ).add( - Config.LIGHT_DROPOFF );
            this.setIllumination( src, curr.max( other ) );
        }
    }

    public void addGlobalIllumination() {
        this.setIllumination( LightSource.GLOBAL, Vector3in.MAX_BYTES );
    }

    public boolean isLit() {
        for( int x : illumination)
            if( x > 0)
                return true;
        return false;
    }

}
