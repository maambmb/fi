package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.BufferedGroup;
import util.Lambda;

public class Listener {

    // An object that listens and relays messages to subscribers
    // subscription is done on a message class basis
    // subs are provided a unique id when they subscribe

    public static Listener GLOBAL;
    public static void init() {
        GLOBAL = new Listener();
    }

    // all sub ids grouped by the class of message they have subscribed to
    private Map<Class<?>,List<Lambda.ActionUnary<Object>>> subscriberGroups;
    private BufferedGroup<Listener> children;

    public Listener() {
        this.subscriberGroups = new HashMap<Class<?>,List<Lambda.ActionUnary<Object>>>();
        this.children = new BufferedGroup<Listener>();
    }

    public <T> void addSubscriber( Class<T> cls, Lambda.ActionUnary<T> subscriber ) {

        // create the group for the class of messages if it doesn't already exist
        // i.e. this is the first subscription for messages of a certain type
        if( !this.subscriberGroups.containsKey( cls ))
            this.subscriberGroups.put( cls, new ArrayList<Lambda.ActionUnary<Object>>() );
        this.subscriberGroups.get( cls ).add( (msg) -> subscriber.run(cls.cast(msg)));
    }
    
    public void addChild( Listener l ) {
    	this.children.add( l );
    }
    
    public void removeChild( Listener l ) {
    	this.children.remove( l );
    }

    public <T> void listen( T msg ) {

    	this.children.sync();
    	for( Listener child : this.children ) {
    		child.listen( msg );
    	}
    	
        // grab the class of the message to find the relevant group of subscribers
        Class<?> cls = msg.getClass();

        // if such a group doesn't exist then nobodys subbing and abort here :(
        if( !this.subscriberGroups.containsKey( cls ) )
            return;

        // copy the ids to a buffer to avoid modifying the collection during an enumeration
        for( Lambda.ActionUnary<Object> fn : this.subscriberGroups.get( cls ) ) {
        	fn.run( msg );
        }
    }

    // wipe everything and reset the id incrementer
    public void reset() {
        for( List<Lambda.ActionUnary<Object>> s : this.subscriberGroups.values() )
            s.clear();
        this.children.reset();
    }
    
}
