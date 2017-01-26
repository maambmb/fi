package game.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.Lambda;

public class Listener {

    public static Listener GLOBAL_LISTENER;
    public static void init() {
        GLOBAL_LISTENER = new Listener();
    }

    private Map<Class<?>,Set<Integer>> listenerGroups;
    private Map<Integer,Lambda.ActionUnary<Object>> listenerMap;
    private Map<Integer,Class<?>> reverseLookup;
    private int listenerId;

    public Listener() {
        this.listenerGroups = new HashMap<Class<?>,Set<Integer>>();
        this.listenerMap    = new HashMap<Integer,Lambda.ActionUnary<Object>>();
        this.reverseLookup  = new HashMap<Integer,Class<?>>();
    }

    public <T> int addListener( Class<T> cls, Lambda.ActionUnary<T> listener ) {
        int newId = this.listenerId ++;
        this.listenerMap.put( newId, o -> listener.run( cls.cast( o ) ) );
        this.reverseLookup.put( newId, cls );
        if( !this.listenerGroups.containsKey( cls ))
            this.listenerGroups.put( cls, new HashSet<Integer>() );
        this.listenerGroups.get( cls ).add( newId );
        return newId;
    }

    public void removeListener( int id ) {
        Class<?> cls = this.reverseLookup.get( id );
        this.reverseLookup.remove( id );
        this.listenerMap.remove( id );
        this.listenerGroups.get( cls ).remove( id );

    }

    public <T> void listen( T msg ) {
        Class<?> cls = msg.getClass();
        if( !this.listenerGroups.containsKey( cls ) )
            return;
        // copy the ids to a buffer to avoid modifying the collection during an enumeration
        Set<Integer> buffer = new HashSet<Integer>( this.listenerGroups.get( cls ) );
        for( Integer ix : buffer )
            this.listenerMap.get( ix ).run( msg );
    }

    public void reset() {
        this.listenerMap.clear();
        this.reverseLookup.clear();
        this.listenerId = 0;
        for( Set<Integer> s : this.listenerGroups.values() )
            s.clear();
    }

    public Client mkClient() {
        return new Client( this );
    }

}
