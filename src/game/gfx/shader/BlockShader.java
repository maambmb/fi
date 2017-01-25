package game.gfx.shader;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL20;

import game.Config;
import game.block.LightSource;
import util.Vector3i;

public class BlockShader extends Shader {

	// map from light source to uniform variable position
	protected Map<LightSource,Integer> lightModulatorMap;
	
	public BlockShader() {
		super();
		this.setup( "", "" );
		this.lightModulatorMap = new HashMap<LightSource,Integer>();
	}
	
	protected void setupUniformVariables() {
		super.setupUniformVariables();
		// loop through all light sources and create a uniform variable for each light source's modulator
		for( LightSource ls : LightSource.values() ) {
			String name = String.format( "uv_light_%s", ls.name().toLowerCase() );
			this.lightModulatorMap.put( ls, this.createUniformVariable( name ) );
		}
	}
	
	public void setLightModulator( LightSource src, Vector3i v ) {
		// set a modulator for a light source
		int pos = this.lightModulatorMap.get( src );
		this.loadVector3i( pos, v );
	}
	
	protected void setupVAOAttributes() {
		super.setupVAOAttributes();
		// loop through all extra data ints, and set them up as attribute list variables
		for( int i = 0; i < Config.BLOCK_VBO_NONPOS_INTS; i += 1 )
			GL20.glBindAttribLocation( this.programId, i + 1, String.format( "ao_extra_%i", i ) );
	}


}
