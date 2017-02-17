package game.particle;

import game.Component;
import game.Entity;
import game.Position3DComponent;
import game.gfx.AttributeVariable;
import game.gfx.BatchElement;
import game.gfx.Model;
import game.gfx.TextureRef;
import util.Vector3fl;

public class ParticleRenderComponent implements Component, BatchElement {

	private ParticleType particleType;
	private Position3DComponent position;

	public ParticleRenderComponent( Entity e, ParticleType particleType ) {
		this.particleType = particleType;
		e.listener.addSubscriber( Position3DComponent.class, x -> this.position = x );
		e.listener.addSubscriber( ParticleShader.ParticleShaderPreRenderMessage.class, this::render );
	}
	
	private void render( ParticleShader.ParticleShaderPreRenderMessage msg ) {
		ParticleShader.GLOBAL.batcher.addToBatch( this );
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void renderToBatch(Model batch) {
		for( Vector3fl v : Model.QUAD_VERTICES ) {
			Vector3fl texCoords = v.multiply(0.5f).add(0.5f).multiply( TextureRef.PARTICLE.elementSize );
			Vector3fl position = v.multiply( 0.5f * this.position.scale );
			position.z = 0;
			batch.addAttributeData( AttributeVariable.POSITION, v.multiply( 0.5f * this.position.scale ) );
			batch.addAttributeData( AttributeVariable.POSITION_WORLD, this.position.position );
			batch.addAttributeData2D( AttributeVariable.TEX_COORDS, texCoords.add( this.particleType.textureCoords ).divide( TextureRef.PARTICLE.size ) );
			batch.addAttributeData( AttributeVariable.COLOR, 0xFFFFFF );
		}
		batch.addQuad();
	}
	
	@Override
	public void destroy() {
		
	}

	@Override
	public int getDepth() {
		return 0;
	}

}
