package game.block;

import util.Packer;
import util.Vector3i;

public class Illumination {

    private Packer lightData;

    public Illumination() {
        // each light source needs 3 bytes for r,g,b
        this.lightData = new Packer( LightSource.values().length * 3 );
    }

    public void clear() {
        this.lightData.clear();
    }

    public void propagate( Vector3i.Normal direction, Illumination otherIllu ) {
        for( LightSource ls : LightSource.values() ) {
            int base = ls.ordinal() * 3;
            for( int i = 0; i < 3; i += 1 ) {
                int ix = base + i;
                int otherVal = otherIllu.lightData.getByte( ix );
                int thisVal = this.lightData.getByte( ix );
                int propagated = Math.max( thisVal, otherVal - ls.dropOff );
                if( direction == Vector3i.Normal.BOTTOM && otherVal == 0xFF && ls == LightSource.GLOBAL )
                    propagated = otherVal;
                this.lightData.addByte( ix, propagated );
            }
        }
    }

    public Illumination set( Illumination illu ) {
        this.lightData.set( illu.lightData );
        return this;
    }

    public Illumination set( LightSource src, Vector3i v ) {
        int base = src.ordinal() * 3;
        for( int i = 0; i < 3; i += 1 )
            this.lightData.addByte( base + i, v.get(i) );
        return this;
    }

}
