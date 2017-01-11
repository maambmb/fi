package util;

public class Vector3i {

	public enum Normal {
		
		FRONT( new Vector3i(0,0,-1) ),
		BACK( new Vector3i(0,0,1) ),
		LEFT( new Vector3i(-1,0,1) ),
		RIGHT( new Vector3i(1,0,0) ),
		TOP( new Vector3i(0,-1,0) ),
		BOTTOM( new Vector3i(0,1,0) );
		
		public static Normal[] NORMALS = { Normal.FRONT, Normal.BACK, Normal.LEFT, Normal.RIGHT, Normal.TOP, Normal.BOTTOM };
		public Vector3i vector;

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
		this.x = x;
		this.y = y;
		this.z = z;
		this.hashCoder = new HashCoder();
	}
	
	public Vector3i() {
		this( 0, 0, 0);
	}

	public Vector3i clone() {
		return new Vector3i( this.x, this.y, this.z );
	}
	
	public Vector3i transform( Lambda.FuncUnary<Integer,Integer> unaryFn ) {
		this.x = unaryFn.run( this.x );
		this.y = unaryFn.run( this.y );
		this.z = unaryFn.run( this.z );
		return this;
	}

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
