package game.block;

import util.Packer;
import util.Vector3i;

// holds illumination/lighting values (r,g,b) for each of the n light sources
public class Illumination {

    // use a data-packer to efficiently hold all the light values
    private Packer lightData;

    public Illumination() {
        // each light source needs 3 bytes for r,g,b
        this.lightData = new Packer( LightSource.values().length * 3 );
    }

    // create an illumination with a light source pre-loaded with a value
    public Illumination( LightSource src, Vector3i v ) {
        this();
        this.set( src, v );
    }

    // propagate illumination from another illum object to this one
    // assuming the other illum object is adjacent and thus the light is attenuated by one round
    public void propagate( Illumination otherIllu ) {
        for( LightSource ls : LightSource.values() ) {
            for( int i = 0; i < 3; i += 1 ) {
                int ix = ls.ordinal() * 3  + i;
                int otherVal = otherIllu.lightData.getByte( ix );
                int thisVal = this.lightData.getByte( ix );
                this.lightData.addByte( ix, Math.max( thisVal, otherVal - ls.dropOff ) );
            }
        }
    }

    // scan through each element of the data packer and find a non-zero int
    // which would signify some light of some sort
    public boolean isLight() {
        for( int i = 0; i < this.lightData.packed.length; i += 1 )
            if( this.lightData.packed[i] > 0 )
                return true;
        return false;
    }

    // set a lightsource at a specified r,g,b level
    public void set( LightSource src, Vector3i v ) {
        int base = src.ordinal() * 3;
        this.lightData.addByte( base, v.x );
        this.lightData.addByte( base + 1, v.y );
        this.lightData.addByte( base + 2, v.z );
    }

    // short hand for turning on direct global lighting
    public void addGlobal() {
        this.set( LightSource.GLOBAL, new Vector3i( 0xFFFFFF ) );
    }

    // transfer illumination from another illum object into this one
    // unlike "propagate", no attenuation occurs
    public void set( Illumination other ) {
        this.lightData.set( other.lightData );
    }

    // wipe the lighting (set everything to 0)
    public void reset() {
        this.lightData.reset();
    }

}
