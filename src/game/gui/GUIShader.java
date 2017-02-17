package game.gui;

import game.Config;
import game.gfx.AttributeVariable;
import game.gfx.Batcher;
import game.gfx.BufferType;
import game.gfx.Model;
import game.gfx.Shader;
import game.gfx.TextureRef;
import game.gfx.UniformVariable;
import util.Matrix4fl;
import util.Vector3fl;

public class GUIShader extends Shader {

	public static class GUIShaderPreRenderMessage {}
	
	private static Matrix4fl matrix = new Matrix4fl();
	
    public static GUIShader GLOBAL;
    public static void setup() {
        GLOBAL = new GUIShader();
    }
    
    public Batcher batcher;
    private Model batchModel;
    
    private GUIShader() {
        super( "glsl/gui/vertex.glsl", "glsl/gui/fragment.glsl" );
        this.batchModel = new Model( TextureRef.GUI, this.getUsedAttributeVariables(), BufferType.STREAM );
        this.batcher = new Batcher();
    }

    @Override
    protected void setupUniformVariables() {
		this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION_2D );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    	this.createAttributeVariable( AttributeVariable.COLOR );
    }
    
    public void render() {
    	this.loadProjectionMatrix();
    	this.batcher.render( this.batchModel );
    }
    
    private void loadProjectionMatrix() {
    	matrix.clearMatrix();
    	matrix.addTranslationToMatrix( new Vector3fl(-1f,1f));
    	matrix.addScaleToMatrix( new Vector3fl(2f,-2f));
    	matrix.addScaleToMatrix(new Vector3fl(
    		1f / Config.GAME_WIDTH,
    		1f/ Config.GAME_HEIGHT
    	));
    	this.loadMatrix4f( UniformVariable.PROJECTION_MATRIX, matrix );
    }
    
    @Override
    public void destroy() {
    	super.destroy();
    	this.batchModel.destroy();
    }
    
    
}
