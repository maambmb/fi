package game.particle;

import game.gfx.Model;
import game.gfx.TextureRef;
import util.Vector3fl;
import util.Vector3in;

public enum ParticleType {

	RAIN( new Vector3in( 1, 0 ), 500, 1 );
	
	public int frames;
	public int lifespanMs;
	public Vector3fl textureCoords;
	public Model model;
	
	public static void setup() {
		for( ParticleType pt : ParticleType.values() )
			pt.buildModel();
	}
	
	private ParticleType( Vector3in textureCoords, int lifespanMs, int frames ) {
		this.textureCoords = textureCoords.multiply( TextureRef.PARTICLE.elementSize ).toVector3fl();
		this.frames = frames;
		this.lifespanMs = lifespanMs;
	}
	
	private void buildModel() {

	}
}
