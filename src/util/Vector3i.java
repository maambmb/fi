package util;

public class Vector3i {

    // unit vectors for normal vectors for all 6 sides of a cube
	public enum Normal {
		
		FRONT( new Vector3i(0,0,-1) ),
		BACK( new Vector3i(0,0,1) ),
		LEFT( new Vector3i(-1,0,1) ),
		RIGHT( new Vector3i(1,0,0) ),
		TOP( new Vector3i(0,-1,0) ),
		BOTTOM( new Vector3i(0,1,0) );

		public Vector3i vector; // the underlying unit vector

		private Normal( Vector3i v ) {
			this.vector = v;
		}
	}
	
	public static final Vector3i ZERO = new Vector3i();

	public int x;
	public int y;
	public int z;

	private HashCoder hashCoder;
		
	public Vector3i( int x, int y, int z ) {
		this.hashCoder = new HashCoder();
		this.x = x;
		this.y = y;
		this.z = z;
	}

    public Vector3i( int packed ) {
		this.hashCoder = new HashCoder();
        this.x = 0xFF & packed;
        packed >>= 8;
        this.y = 0xFF & packed;
        packed >>= 8;
        this.z = 0xFF & packed;
    }

    public Vector3i( Vector3i v ) {
        this( v.x, v.y, v.z );
    }
	
	public Vector3i() {
		this( 0, 0, 0);
	}

    public Vector3i explode( Vector2i v, Vector3i.Normal n ) {
        int projIx = 0;
        for( int i = 0; i < 3; i += 1 )
            if( n.vector.get(i) == 0 )
                v.set( i, v.get( projIx ++ ) );
        return this;
    }

	public Vector3i clone() {
		return new Vector3i( this.x, this.y, this.z );
	}
	
    // transform the vector by operating on each element using a unary function self.x = f(self.x)
	public Vector3i transform( Lambda.FuncUnary<Integer,Integer> unaryFn ) {
		this.x = unaryFn.run( this.x );
		this.y = unaryFn.run( this.y );
		this.z = unaryFn.run( this.z );
		return this;
	}

    // transform the vector by operating on each element using a unary function self.x = f(self.x,other.x)
    // where other is the other vector provided in the 1st argument
	public Vector3i transform( Vector3i v, Lambda.FuncBinary<Integer,Integer,Integer> binaryFn ) {
		this.x = binaryFn.run( this.x, v.x );
		this.y = binaryFn.run( this.y, v.y );
		this.z = binaryFn.run( this.z, v.z );
		return this;
	}
	
	public Vector3i add( Vector3i v ) {
		return this.transform( v, (x,y) -> x + y );
	}
	
	public Vector3i negate() {
		return this.transform( (x) -> - x );
	}
	
	public Vector3i reset() {
		return this.transform( (_x) -> 0 );
	}

	public Vector3i set( Vector3i v ) {
		return this.transform( v, (_x,y) -> y );
	}
	
	public Vector3i divide( int dsor ) {
		return this.transform( x -> x / dsor );
	}
	
	public Vector3i multiply( int mult ) {
		return this.transform( x -> x * mult );
	}
	
	public Vector3i modulo( int mod ) {
		return this.transform( x -> x % mod );
	}

    public int get( int ix) {
        if( ix == 0 )
            return this.x;
        if( ix == 1 )
            return this.y;
        if( ix == 2 )
            return this.z;
        throw new IndexOutOfBoundsException();
    }

    public Vector3i set( int ix, int val ) {
        if( ix == 0 )
            this.x = val;
        else if( ix == 1 )
            this.y = val;
        else if( ix == 2 )
            this.z = val;
        else
            throw new IndexOutOfBoundsException();
        return this;
    }

	@Override
	public int hashCode() {
		this.hashCoder.reset();
		this.hashCoder.addHash( this.x );
		this.hashCoder.addHash( this.y );
		this.hashCoder.addHash( this.z );
		return this.hashCoder.hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		Vector3i other = (Vector3i) obj;
		return x == other.x && y == other.y && z == other.z;
	}
	
	
}
