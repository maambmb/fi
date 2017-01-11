package util;

public class Tuple {

	public static class Binary<T1,T2> {
	
		public T1 arg1;
		public T2 arg2;
		
		private Binary( T1 arg1, T2 arg2) {
			this.arg1 = arg1;
			this.arg2 = arg2;
		}
		
	}

	public static class Ternary<T1,T2,T3> {
	
		public T1 arg1;
		public T2 arg2;
		public T3 arg3;
		
		private Ternary( T1 arg1, T2 arg2, T3 arg3) {
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.arg3 = arg3;
		}
		
	}
	
	public static <T1,T2> Binary<T1,T2> create( T1 arg1, T2 arg2 ) {
		return new Binary<T1, T2>( arg1, arg2 );
	}

	public static <T1,T2,T3> Ternary<T1,T2,T3> create( T1 arg1, T2 arg2, T3 arg3 ) {
		return new Ternary<T1, T2, T3>( arg1, arg2, arg3 );
	}
	
}
