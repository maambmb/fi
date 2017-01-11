package util;

public class Lambda {

	public interface ActionNullary {
		void run();
	}
	
	public interface ActionUnary<T> {
		void run( T arg );
	}
	
	public interface ActionBinary<T1,T2> {
		void run( T1 arg1, T2 arg2 );
	}
	
	public interface FuncNullary<T> {
		T run();
	}
	
	public interface FuncUnary<T1,T2> {
		T1 run( T2 arg );
	}
	
	public interface FuncBinary<T1,T2,T3> {
		T1 run( T2 arg1, T3 arg2 );
	}
	
}
