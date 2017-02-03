package util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BufferedGroup<T> implements Iterable<T> {

	private Set<T> baseSet;
	private Set<T> addSet;
	private Set<T> removeSet;
	
	public BufferedGroup() {
		this.baseSet = new HashSet<T>();
		this.addSet = new HashSet<T>();
		this.removeSet = new HashSet<T>();
	}

	@Override
	public Iterator<T> iterator() {
		return this.baseSet.iterator();
	}
	
	public void sync() {
		for( T el : this.removeSet )
			this.baseSet.remove( el );
		for( T el : this.addSet )
			this.baseSet.add( el );
		this.addSet.clear();
		this.removeSet.clear();
	}
	
	public void add( T el ) {
		this.addSet.add( el );
		this.removeSet.remove( el );
	}
	
	public void remove( T el ) {
		this.addSet.remove( el );
		this.removeSet.add( el );
	}
	
	public void reset() {
		this.baseSet.clear();
		this.addSet.clear();
		this.removeSet.clear();
	}
	
}
