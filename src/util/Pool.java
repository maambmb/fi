package util;

import java.util.LinkedList;
import java.util.Queue;

public class Pool<T extends Pool.Poolable> {

	public interface Poolable {
		void destroy();
		void init();
	}
	
	private Queue<T> freeQueue;
	private int maxSize;
	private int current;
	private Lambda.FuncNullary<T> generator;
	
	public Pool( Lambda.FuncNullary<T> generator ) {
		this.generator = generator;
		this.freeQueue = new LinkedList<T>();
		this.current = 0;
		this.maxSize = Integer.MAX_VALUE;
	}
	
	public Pool( Lambda.FuncNullary<T> generator, int maxSize ) {
		this( generator );
		this.maxSize = maxSize;
	}
	
	public T fresh() {
		if( this.freeQueue.isEmpty() ) {
			if( this.current == this.maxSize )
				throw new RuntimeException( "Pool has reached capacity" );
			this.freeQueue.add( this.generator.run() );
		}
		this.current += 1;
		T fresh = this.freeQueue.remove();
		fresh.init();
		return fresh;
	}
	
	public void reclaim( T toReclaim ) {
		this.freeQueue.add( toReclaim );
		this.current -= 1;
	}
	
}
