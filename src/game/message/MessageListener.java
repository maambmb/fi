package game.message;

import java.util.HashMap;
import java.util.Map;

import util.Lambda;

public class MessageListener {

	public static MessageListener GLOBAL_LISTENER = new MessageListener();
	
	private Map<Integer,Lambda.ActionUnary<Message>> listenerMap;
	private int listenerId;
	
	// convert a type specific listener to a generic one that ignores all messages that not of said type
	public static <T extends Message> Lambda.ActionUnary<Message> listenerTryWrap( Class<T> cls, Lambda.ActionUnary<T> listener ) {
		return (msg) -> { if( cls.isInstance( msg ) ) listener.run( cls.cast( msg ) ); };
	}
	
	public MessageListener() {
		this.listenerId = 0;
		this.listenerMap = new HashMap<Integer, Lambda.ActionUnary<Message>>();
	}
	
	public int addListener( Lambda.ActionUnary<Message> listener ) {
		int newId = this.listenerId ++; 
		this.listenerMap.put( newId, listener );
		return newId;
	}
	
	public void removeListener( int id ) {
		this.listenerMap.remove( id );
	}
	
	public void reset() {
		this.listenerMap.clear();
	}
	
	public void listen( Message msg ) {
		for( Lambda.ActionUnary<Message> listener : this.listenerMap.values() )
			listener.run( msg );
	}

}
