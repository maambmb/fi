package game.listener;

import java.util.ArrayList;
import java.util.List;

import util.Lambda;

public class Client {

    private List<Integer> listenerIds;
    private Listener listener;

    public Client( Listener listener ) {
        this.listener = listener;
        this.listenerIds = new ArrayList<Integer>();
    }

    public <T> void addListener( Class<T> cls, Lambda.ActionUnary<T> listener ) {
        int newId =  this.listener.addListener( cls, listener );
        this.listenerIds.add( newId );
    }

    public void removeListeners() {
        for( int id : this.listenerIds )
            this.listener.removeListener( id );

    }
}
