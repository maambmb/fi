package game;

import java.util.ArrayList;
import java.util.List;
import game.Game.DestroyMessage;

public abstract class Entity {

    // message listener for components specific to just this entity
    public Listener listener;
    // *all* components registered to this entity
    private List<Component> componentList;

    protected Entity() {
        this.listener = new Listener();
        this.listener.addSubscriber(DestroyMessage.class, this::destroy );
        this.componentList = new ArrayList<Component>();
    }

    protected void build() {
        for( Component c : this.componentList )
            this.listener.listen( c );
        this.init();
    }

    protected <T extends Component> T registerComponent( T c ) {
        this.componentList.add( c );
        return c;
    }

    public <T> void listen( T msg ) {
        this.listener.listen( msg );
    }
    
    private void destroy( DestroyMessage m ) {
    	this.destroy();
    }
    
    protected void init() {
        for( Component c : this.componentList )
            c.init();
    }

    public void destroy() {
        for( Component c : this.componentList )
            c.destroy();
    }
    

}
