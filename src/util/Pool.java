package util;

import java.util.LinkedList;
import java.util.Queue;

public class Pool<T extends Poolable> {

	private Queue<T> internalQueue;
	private Lambda.FuncNullary<T> generator;
	private int capacity;
	private int currentSize;

	public Pool( Lambda.FuncNullary<T> generator ) {
		this( generator, Integer.MAX_VALUE );
	}
	
	public Pool( Lambda.FuncNullary<T> generator, int capacity ) {
		this.generator = generator;
		this.internalQueue = new LinkedList<T>();
		this.capacity = capacity;
		this.currentSize = 0;
	}

	public T tryFresh() {
		if( this.internalQueue.size() == 0 ) {
			if( this.currentSize == this.capacity )
				return null;
			this.currentSize += 1;
			this.internalQueue.add( this.generator.run() );
		}
		T out = this.internalQueue.remove();
		out.init();
		return out;
	}
	
	public T fresh() {
		T val = this.tryFresh();
		if( val == null )
			throw new RuntimeException( "Pool capacity reached" );
		return val;
	}
	
	public void reclaim( T el ) {
		this.internalQueue.add( el );
	}
	
}
