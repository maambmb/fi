package game.particle;

import game.Entity;
import game.GlobalSubscriberComponent;
import game.Position3DComponent;
import game.gfx.UniformVariable;
import game.gui.GUIShader;
import util.Matrix4fl;
import util.Pool;
import util.Vector3in;

public class Particle extends Entity {

	public static Pool<Particle> POOL = new Pool<Particle>( Particle::new );
	private static Matrix4fl matrix = new Matrix4fl();

	private ParticleType particleType;
	private float age;
	private Vector3in color;
	private Position3DComponent position;
	
	private Particle() {
	}
	
	public void init() {
		super.init();
		this.age = 0;
	}

	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
		this.position = this.registerComponent( new Position3DComponent() );
	}
	
	private void render() {
    	int xCur = 0;
    	int yCur = 0;
    	
    	matrix.clearMatrix();
    	matrix.addTranslationToMatrix( this.position.position );
    	matrix.addScaleToMatrix( this.position.scale );
    	GUIShader.GLOBAL.loadMatrix4f( UniformVariable.MODEL, matrix );

	}
	
}
