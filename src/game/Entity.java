package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.component.Component;

public abstract class Entity {
	  
	// message listener for components specific to just this entity
	public Listener listener;
	// *all* components registered to this entity
	private List<Component> componentList;
	// public + accessible components registered to this entity
	private Map<Class<? extends Component>,Component> componentMap;
	
	protected abstract void addComponents();
	
	private Entity() {
		this.listener = new Listener();
		this.componentList = new ArrayList<Component>();
		this.componentMap = new HashMap<Class<? extends Component>,Component>();

		this.addComponents();
		for( Component c : this.componentList )
			c.setup( this );
	}
	
	protected void addComponent( Component c ) {
		this.componentList.add( c );
	}
	
	protected <T extends Component> void addComponent( Class<T> cls, T cmpt ) {
		this.addComponent( cmpt );
		this.componentMap.put( cls, cmpt );
	}
	
	public <T extends Component> T getComponent( Class<T> cls ) {
		Component cmpt = this.componentMap.get( cls );
		return cmpt == null ? null : cls.cast( cmpt );
	}
		
	public void destroy() {
        // destroy all components by looping through map
		for( Component c : this.componentList )
			c.destroy();
	}
	
}
