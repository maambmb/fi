package game.component;

import util.Pool;

public final class Position3DComponent implements Component {
	
	public static Pool<Position3DComponent> POOL = new Pool<Position3DComponent>( Position3DComponent::new );
	
	private Position3DComponent() {

	}

	@Override
	public void init() {
		
	}

	@Override
	public void destroy() {
		POOL.reclaim(this);
	}



	
}
