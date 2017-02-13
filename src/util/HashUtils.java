package util;

// utility object for computing hashcodes
public class HashUtils {

    public static int hash( Object... objs ) {
    	int hash = 1;
    	for( Object o : objs ) {
    		hash += o.hashCode();
    		hash *= 31;
    	}
    	return hash;
    }
    
}
