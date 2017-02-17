package game;

import java.util.ArrayList;
import java.util.List;

import game.Game.DestroyMessage;
import util.Poolable;

public abstract class Entity implements Poolable {

    // message listener for components specific to just this entity
    public Listener listener;
    // *all* components registered to this entity
    private List<Component> componentList;

    protected abstract void registerComponents();

    protected Entity() {
        this.listener = new Listener();
        this.componentList = new ArrayList<Component>();
        this.build();
        
        this.listener.addSubscriber(DestroyMessage.class, (x) -> this.destroy() );
    }

    private void build() {
        this.registerComponents();

        for( Component c : this.componentList )
            c.setup( this );
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
    
    public void init() {
        for( Component c : this.componentList )
            c.init();
    }
    
    public void destroy() {
        for( Component c : this.componentList )
            c.destroy();
    }
    

}
