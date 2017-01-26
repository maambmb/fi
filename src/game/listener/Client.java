package game.listener;

import java.util.ArrayList;
import java.util.List;

import util.Lambda;

public class Client {

    // manages a collection of subscribers for a single listener
    // chiefly is able to mass unsubscribe all managed subscribers at once

    private List<Integer> subscriberIds;
    private Listener listener;

    public Client( Listener listener ) {
        this.listener = listener;
        this.subscriberIds = new ArrayList<Integer>();
    }

    public <T> void addSubscriber( Class<T> cls, Lambda.ActionUnary<T> listener ) {
        int newId =  this.listener.addSubcriber( cls, listener );
        this.subscriberIds.add( newId );
    }

    public void removeSubscribers() {
        for( int id : this.subscriberIds )
            this.listener.removeSubscriber( id );

    }
}
