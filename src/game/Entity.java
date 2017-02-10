package game;

import java.util.ArrayList;
import java.util.List;

import game.Game.DestroyMessage;

public abstract class Entity {

    // message listener for components specific to just this entity
    public Listener listener;
    // *all* components registered to this entity
    private List<Component> componentList;

    protected abstract void registerComponents();

    protected Entity() {
        this.listener = new Listener();
        this.componentList = new ArrayList<Component>();
        this.setup();
        
        this.listener.addSubscriber(DestroyMessage.class, (x) -> this.destroy() );
    }

    private void setup() {

        this.registerComponents();

        for( Component c : this.componentList )
            c.setup( this );
        for( Component c : this.componentList )
            this.listener.listen( c );
        for( Component c : this.componentList)
        	c.init();
    }

    protected <T extends Component> T registerComponent( T c ) {
        this.componentList.add( c );
        return c;
    }

    public <T> void listen( T msg ) {
        this.listener.listen( msg );
    }

    public void destroy() {
        for( Component c : this.componentList )
            c.destroy();
    }
    

}
