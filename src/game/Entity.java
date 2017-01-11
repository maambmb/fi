package game;


import java.util.HashMap;
import java.util.Map;

import game.component.Component;
import game.component.Position3DComponent;
import game.message.Message;
import game.message.MessageListener;
import util.Lambda;
import util.Pool;

public class Entity implements Pool.Poolable {
	
	public static Pool<Entity> POOL = new Pool<Entity>( Entity::new );
	
	private Map<Component.Type,Component> componentMap;
	private MessageListener messageListener;
	private int globalListenerId;
	
	private Entity() {
		this.componentMap = new HashMap<Component.Type,Component>();
		this.messageListener = new MessageListener();
	}
	
	public <T extends Message> void addListener( Class<T> cls, Lambda.ActionUnary<T> listener ) {
		this.messageListener.addListener( MessageListener.listenerTryWrap( cls, listener) );
	}
	
	public void addComponent( Component.Type type, Component component ) {
		this.componentMap.put( type, component );
	}
	
	public <T extends Component> T getComponent( Class<T> cls, Component.Type type ) {
		if( this.componentMap.containsKey( type ) )
			return cls.cast( this.componentMap.get( type ) );
		return null;
	}
	
	public void listen( Message msg ) {
		this.messageListener.listen( msg );
	}
	
	public void destroy() {
		POOL.reclaim(this);
		for( Component c : this.componentMap.values() )
			c.destroy();
		this.componentMap.clear();
		this.messageListener.reset();
		MessageListener.GLOBAL_LISTENER.removeListener( this.globalListenerId );
	}
	
	@Override
	public void init() {
		this.globalListenerId = MessageListener.GLOBAL_LISTENER.addListener( this::listen );
	}

	/* Component access functions below */
	
	public Position3DComponent getPosition3DComponent() {
		return this.getComponent( Position3DComponent.class, Component.Type.Position3D );
	}

}
