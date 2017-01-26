package game.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.Lambda;

public class Listener {

    // An object that listens and relays messages to subscribers
    // subscription is done on a message class basis
    // subs are provided a unique id when they subscribe

    public static Listener GLOBAL_LISTENER;
    public static void init() {
        GLOBAL_LISTENER = new Listener();
    }

    // all sub ids grouped by the class of message they have subscribed to
    private Map<Class<?>,Set<Integer>> subscriberGroups;

    // the map from the sub id to the underlying listener object
    private Map<Integer,Lambda.ActionUnary<Object>> subscriberMap;

    // a reverse lookup to find the class of a sub from its ids
    private Map<Integer,Class<?>> reverseLookup;

    // an id incrementer
    private int subscriberId;

    public Listener() {
        this.subscriberGroups = new HashMap<Class<?>,Set<Integer>>();
        this.subscriberMap    = new HashMap<Integer,Lambda.ActionUnary<Object>>();
        this.reverseLookup    = new HashMap<Integer,Class<?>>();
    }

    public <T> int addSubcriber( Class<T> cls, Lambda.ActionUnary<T> subscriber ) {
        // get a unique id and increment
        int newId = this.subscriberId ++;

        // degenerify the sub via wrapping and a necessarily safe cast
        this.subscriberMap.put( newId, o -> subscriber.run( cls.cast( o ) ) );

        // add the reverse lookup entry
        this.reverseLookup.put( newId, cls );

        // create the group for the class of messages if it doesn't already exist
        // i.e. this is the first subscription for messages of a certain type
        if( !this.subscriberGroups.containsKey( cls ))
            this.subscriberGroups.put( cls, new HashSet<Integer>() );

        // assign the sub to the group
        this.subscriberGroups.get( cls ).add( newId );

        // return the id!
        return newId;
    }

    public void removeSubscriber( int id ) {
        // make sure to remove the id from the sub map,
        // reverse lookup and the relevant sub group
        Class<?> cls = this.reverseLookup.get( id );
        this.reverseLookup.remove( id );
        this.subscriberMap.remove( id );
        this.subscriberGroups.get( cls ).remove( id );

    }

    public <T> void listen( T msg ) {

        // grab the class of the message to find the relevant group of subscribers
        Class<?> cls = msg.getClass();

        // if such a group doesn't exist then nobodys subbing and abort here :(
        if( !this.subscriberGroups.containsKey( cls ) )
            return;

        // copy the ids to a buffer to avoid modifying the collection during an enumeration
        Set<Integer> buffer = new HashSet<Integer>( this.subscriberGroups.get( cls ) );
        for( Integer ix : buffer )
            this.subscriberMap.get( ix ).run( msg );
    }

    // wipe everything and reset the id incrementer
    public void reset() {
        this.subscriberMap.clear();
        this.reverseLookup.clear();
        this.subscriberId = 0;
        for( Set<Integer> s : this.subscriberGroups.values() )
            s.clear();
    }

    // generate a client (manages a collection of subscribers)
    public Client mkClient() {
        return new Client( this );
    }

}
