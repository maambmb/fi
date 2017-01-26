package game;

import java.util.ArrayList;
import java.util.List;

import game.component.Component;
import game.listener.Client;
import game.listener.Listener;

public abstract class Entity {

    // message listener for components specific to just this entity
    public Listener listener;
    public Client globalListenerClient;
    // *all* components registered to this entity
    private List<Component> componentList;

    protected abstract void addComponents();

    protected Entity() {
        this.listener             = new Listener();
        this.globalListenerClient = Listener.GLOBAL_LISTENER.mkClient();
        this.componentList        = new ArrayList<Component>();

        this.addComponents();
        for( Component c : this.componentList )
            c.setup( this );
        for( Component c : this.componentList )
            this.listener.listen( c );

        this.globalListenerClient.addSubscriber( Game.DestroyMessage.class, (_m) -> this.destroy() );
    }

    protected void addComponent( Component c ) {
        this.componentList.add( c );
    }

    public <T> void listen( T msg ) {
        this.listener.listen( msg );
    }

    public void destroy() {
        this.globalListenerClient.removeSubscribers();
        for( Component c : this.componentList )
            c.destroy();
    }

}
