package game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.component.Component;
import util.Pool;

public class Entity implements Pool.Poolable {
	
	public static Pool<Entity> POOL = new Pool<Entity>( Entity::new );
	
	private Map<Component.Type,Component> componentMap;
	private List<Component.ToUpdate> toUpdate;
	private List<Component.ToDraw> toDraw;
	
	private Entity() {
		this.componentMap = new HashMap<Component.Type,Component>();
		this.toUpdate = new ArrayList<Component.ToUpdate>();
		this.toDraw = new ArrayList<Component.ToDraw>();
	}
	
	protected void registerComponent( Component.Type type, Component component ) {
		if( component instanceof Component.ToUpdate)
			this.toUpdate.add( (Component.ToUpdate) component );
		if( component instanceof Component.ToDraw)
			this.toDraw.add( (Component.ToDraw) component );
		this.componentMap.put( type, component );
	}
		
	@Override
	public void init() {
		// nothing needs to be done on init
	}
	
	@Override
	public void destroy() {
		for( Component c : this.componentMap.values() )
			c.destroy();
		this.componentMap.clear();
		this.toUpdate.clear();
		this.toDraw.clear();
		POOL.reclaim(this);
	}
	
	/* Game loop functions below */

	public void update( long deltaMs ) {
		for( Component.ToUpdate cmpt : this.toUpdate )
			cmpt.update( deltaMs );
	}
	
	public void drawStart() {
		for( Component.ToDraw cmpt : this.toDraw )
			cmpt.drawStart();
	}

	public void draw() {
		for( Component.ToDraw cmpt : this.toDraw )
			cmpt.draw();
	}
	
	public void drawEnd() {
		for( Component.ToDraw cmpt : this.toDraw )
			cmpt.drawEnd();
	}

}
