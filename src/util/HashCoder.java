package util;

public class HashCoder {
	
	public int hash;
	
	public HashCoder() {
		this.hash = 1;
	}
	
	public void addHash( Object o ) {
		this.hash += o.hashCode();
		this.hash *= 31;
	}
	
	public void reset() {
		this.hash = 1;
	}
	
}
