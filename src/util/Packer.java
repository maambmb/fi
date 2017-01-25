package util;

// utility object for efficiently packing bytes, words and dwords in an array of integers
public class Packer {

    private static final int BYTE_MASK = 0xFF;
    private HashCoder hashCoder;

    // container for the packed bytes
    public int[] packed;

    // specify the number of bytes up-front so we can allocate the container array
    public Packer( int numBytes ) {
        this.packed = new int[ (int)Math.ceil( numBytes / 4 ) ];
        this.hashCoder = new HashCoder();
    }

    // reset the packer, i.e. zero everything
    public Packer reset() {
        for( int i = 0; i < this.packed.length; i += 1 )
            this.packed[i] = 0;
        return this;
    }

    // copy the contents of another packer into this one
    public Packer set( Packer other ) {
        for( int i = 0; i < this.packed.length; i += 1 )
            this.packed[i] = other.packed[i];
        return this;
    }

    // add a byte at an arbitrary position
    public void addByte( int pos, int val ) {
        // find the integer that we are storing the byte in
        int ix = pos / 4;
        // find the offset in the integer
        int offset = 8 * ( pos % 4 );
        // mask out the required region (zero it)
        this.packed[ ix ] &= ~ ( BYTE_MASK << offset );
        // now you can OR in the byte
        this.packed[ ix ] |= val << offset;
    }

    // add n consecutive bytes starting at an arbitrary position
    public void addN( int pos, int n, int val ) {
        for( int i = 0; i < n; i += 1 ) {
            this.addByte( pos + i, val & BYTE_MASK );
            val >>= 8;
        }
    }

    // add 2 consecutive bytes
    public void addWord( int pos, int word ) {
        this.addN( pos, 2, word );
    }

    // add 4 consecutive bytes
    public void addDWord( int pos, int dword ) {
        this.addN( pos, 4, dword );
    }

    // get a byte from an arbitrary position
    public int getByte( int pos ) {
        // find the int that holds the byte
        int ix = pos / 4;
        // find the offset of the byte within the int
        int offset = 8 * ( pos % 4 );
        // shift the int down so the value is at the 8 LSBs and AND out the remaining data
        return ( this.packed[ix] >> offset ) & BYTE_MASK;
    } 

    // get n conseuctive bytes
    public int getN( int pos, int n ) {
        int result = 0;
        int shifts = 0;
        // loop n times, each time increasing the amount we bit shift by increments of 8
        // such that the first element has the 8 LSBs and the last has the 8 MSBs.
        // add the result into a results tally and return
        for( int i = 0; i < n; i += 1 ) {
            result += this.getByte( pos + i ) << shifts;
            shifts += 8;
        }
        return result;
    }

    // get 2 consecutive bytes
    public int getWord( int pos ) {
        return this.getN( pos, 2 );
    }

    // get 4 consecutive bytes
    public int getDWord( int pos ) {
        return this.getN( pos, 4 );
    }

    @Override
    public int hashCode() {
        this.hashCoder.reset();
        for( int p : this.packed )
            this.hashCoder.addHash( p );
        return this.hashCoder.hash;
    }

    @Override
    public boolean equals( Object obj ) {
        if( getClass() != obj.getClass() )
            return false;
        Packer other = (Packer) obj;
        if( this.packed.length != other.packed.length )
            return false;
        for( int i = 0; i < this.packed.length; i += 1 )
            if( this.packed[i] != other.packed[i] )
                return false;
        return true;
    }

}
