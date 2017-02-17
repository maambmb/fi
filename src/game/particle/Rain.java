package game.particle;

import game.Entity;
import game.GlobalSubscriberComponent;
import game.Position3DComponent;
import game.Game.UpdateMessage;

public class Rain extends Entity {
	
	public Position3DComponent position; 
	private int ageMs;
	
	public Rain() {
		this.position = this.registerComponent( new Position3DComponent() );
		this.registerComponent( new GlobalSubscriberComponent( this ) );
		this.registerComponent( new ParticleRenderComponent( this, ParticleType.RAIN ) );
		this.listener.addSubscriber( UpdateMessage.class, this::update );
		this.build();
	}

	private void update( UpdateMessage m ) {
		this.position.position.y -= 1;
		this.ageMs += m.deltaMs;
		if( this.ageMs > ParticleType.RAIN.lifespanMs )
			this.destroy();
			
	}

}
