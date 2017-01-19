package util;

public class Packer {

    private static final int BYTE_MASK = 0xFF;
    private int[] packed;

    public Packer( int numBytes ) {
        this.packed = new int[ (int)Math.ceil( numBytes / 4 ) ];
    }

    public void clear() {
        for( int i = 0; i < this.packed.length; i += 1 )
            this.packed[i] = 0;
    }

    public void set( Packer other ) {
        for( int i = 0; i < this.packed.length; i += 1 )
            this.packed[i] = other.packed[i];
    }

    public void addByte( int pos, int val ) {
        int ix = pos / 4;
        int offset = 8 * ( pos % 4 );

        this.packed[ ix ] &= ~ ( BYTE_MASK << offset );
        this.packed[ ix ] |= val << offset;
    }

    private void addN( int pos, int n, int val ) {
        for( int i = 0; i < n; i += 1 ) {
            this.addByte( pos + i, val & BYTE_MASK );
            val >>= 8;
        }
    }

    public void addWord( int pos, int word ) {
        this.addN( pos, 2, word );
    }

    public void addDWord( int pos, int dword ) {
        this.addN( pos, 4, dword );
    }

    public int getByte( int pos ) {
        int ix = pos / 4;
        int offset = 8 * ( pos % 4 );
        return ( this.packed[ix] >> offset ) & BYTE_MASK;
    } 

    private int getN( int pos, int n ) {
        int result = 0;
        int shifts = 0;
        for( int i = 0; i < n; i += 1 ) {
            result += this.getByte( pos + i ) << shifts;
            shifts += 8;
        }
        return result;
    }

    public int getWord( int pos ) {
        return this.getN( pos, 2 );
    }

    public int getDWord( int pos ) {
        return this.getN( pos, 4 );
    }

}
