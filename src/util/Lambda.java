package util;

// type defs for anonymous functions
public class Lambda {

    // actions are return type void

    public interface ActionNullary {
        void run();
    }

    public interface ActionUnary<T> {
        void run( T arg );
    }

    public interface ActionBinary<T1,T2> {
        void run( T1 arg1, T2 arg2 );
    }

    public interface ActionTernary<T1,T2,T3> {
        void run( T1 arg1, T2 arg2, T3 arg3 );
    }

    // funcs have some non-void return type

    public interface FuncNullary<T> {
        T run();
    }

    public interface FuncUnary<T1,T2> {
        T2 run( T1 arg );
    }

    public interface FuncBinary<T1,T2,T3> {
        T3 run( T1 arg1, T2 arg2 );
    }

}
