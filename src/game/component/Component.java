package game.component;

import game.Entity;

public interface Component {
	
	void destroy();
	
	void setup( Entity e );
	
}
