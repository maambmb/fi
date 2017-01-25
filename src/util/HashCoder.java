package util;

// utility object for computing hashcodes
public class HashCoder {

    public int hash;

    public HashCoder() {
        this.reset();
    }

    // add another field/component to the hash calculation
    public void addHash( Object o ) {
        this.hash += o.hashCode();
        this.hash *= 31;
    }

    // reset the hash val in preparation for the next calc
    public void reset() {
        this.hash = 1;
    }

}
