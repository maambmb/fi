package util;

public class Vector2i {

	public enum Normal {
		
		FRONT( new Vector2i(0,-1) ),
		BACK( new Vector2i(0,1) ),
		LEFT( new Vector2i(-1,0) ),
		RIGHT( new Vector2i(1,0) );
		
		public static Normal[] NORMALS = { Normal.FRONT, Normal.BACK, Normal.LEFT, Normal.RIGHT };
		public Vector2i vector;

		private Normal( Vector2i v ) {
			this.vector = v;
		}
	}
	
	public static final Vector2i ZERO = new Vector2i();

	public int x;
	public int z;

	private HashCoder hashCoder;
		
	public Vector2i( int x, int z ) {
		this.x = x;
		this.z = z;
		this.hashCoder = new HashCoder();
	}
	
	public Vector2i() {
		this( 0, 0);
	}

	public Vector2i clone() {
		return new Vector2i( this.x, this.z );
	}
	
	public Vector2i transform( Lambda.FuncUnary<Integer,Integer> unaryFn ) {
		this.x = unaryFn.run( this.x );
		this.z = unaryFn.run( this.z );
		return this;
	}

	public Vector2i transform( Vector2i v, Lambda.FuncBinary<Integer,Integer,Integer> binaryFn ) {
		this.x = binaryFn.run( this.x, v.x );
		this.z = binaryFn.run( this.z, v.z );
		return this;
	}
	
	public Vector2i add( Vector2i v ) {
		return this.transform( v, (x,z) -> x + z );
	}
	
	public Vector2i negate() {
		return this.transform( (x) -> - x );
	}
	
	public Vector2i reset() {
		return this.transform( (_x) -> 0 );
	}

	public Vector2i set( Vector2i v ) {
		return this.transform( v, (_x,z) -> z );
	}
	
	public Vector2i set( Vector3i v ) {
		this.x = v.x;
		this.z = v.z;
		return this;
	}
	
	public Vector2i setX( int x ) {
		this.x = x;
		return this;
	}
	
	public Vector2i setZ( int z ) {
		this.z = z;
		return this;
	}
	
	public Vector2i divide( int dsor ) {
		return this.transform( x -> x / dsor );
	}
	
	public Vector2i multiply( int mult ) {
		return this.transform( x -> x * mult );
	}
	
	public Vector2i modulo( int mod ) {
		return this.transform( x -> x % mod );
	}

	@Override
	public int hashCode() {
		this.hashCoder.reset();
		this.hashCoder.addHash( this.x );
		this.hashCoder.addHash( this.z );
		return this.hashCoder.hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (getClass() != obj.getClass())
			return false;
		Vector2i other = (Vector2i) obj;
		return x == other.x && z == other.z;
	}
	
	
}
