package util;

import java.util.LinkedList;
import java.util.Queue;

// an object pool to manage and re-use objects instead of relying on the GC
public class Pool<T extends Pool.Poolable> {

	public interface Poolable {
		void destroy(); // clean up any resources and also become reclaimed by the pool
		void init();    // set up any required resources
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
	
    // override the infinite max capacity to something more finite
    // will throw if this is breached
	public Pool( Lambda.FuncNullary<T> generator, int maxSize ) {
		this( generator );
		this.maxSize = maxSize;
	}
	
    // either generate a brand new object, or recycle an already reclaimed object 
	public T fresh() {
        // if the queue is empty we must generate a fresh object
        // ensuring we add 1 to the capacity tally
		if( this.freeQueue.isEmpty() ) {
			if( this.current == this.maxSize )
                // too many have been produced...
				throw new RuntimeException( "Pool has reached capacity" );
            this.current += 1;
			this.freeQueue.add( this.generator.run() );
		}
		T fresh = this.freeQueue.remove();
		fresh.init();
		return fresh;
	}
	
    // add an object back into a pool of re-usables
	public void reclaim( T toReclaim ) {
		this.freeQueue.add( toReclaim );
	}
	
}
