package game.component;

import util.Pool;

public interface Component extends Pool.Poolable {
	
	public interface ToUpdate {
		void update( long deltaMs );
	}
	
	public interface ToDraw {
		void drawStart();
		void draw();
		void drawEnd();
	}

	public enum Type {
		Position3D
	}
	
}
