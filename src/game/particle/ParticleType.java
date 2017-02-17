package game.particle;

import game.Config;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.TextureRef;
import util.Vector3fl;
import util.Vector3in;

public enum ParticleType {

	LIGHT_RAIN( Vector3in.ZERO, 1 ),
	MEDIUM_RAIN( new Vector3in( 1, 0 ), 1 ),
	HEAVY_RAIN( new Vector3in( 2, 0 ), 1 );
	
	public int frames;
	public Vector3in textureCoords;
	public Model model;
	
	public static void setup() {
		for( ParticleType pt : ParticleType.values() )
			pt.buildModel();
	}
	
	private ParticleType( Vector3in textureCoords, int frames ) {
		this.textureCoords = textureCoords;
		this.frames = frames;
	}
	
	private void buildModel() {

	}
}
