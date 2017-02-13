package util;

// utility object for computing hashcodes
public class HashUtils {

    private static int accumulate( int old, Object o ) {
    	old += o.hashCode();
    	return old * 31;
    }
    
    public static int hash( Object o1, Object o2 ) {
    	int hash = accumulate( 1, o1 );
    	return accumulate( hash, o2 );
    }
    
    public static int hash( Object o1, Object o2, Object o3 ) {
    	int hash = hash( o1, o2 );
    	return accumulate( hash, o3 );
    }
    
}
