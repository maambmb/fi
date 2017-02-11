package game.gui;

import org.lwjgl.util.vector.Matrix4f;

import game.Config;
import game.gfx.AttributeVariable;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import util.MatrixUtils;
import util.Vector3fl;

public class GUIShader extends Shader {

	public static class GUIShaderRenderMessage {
		public GUIDepth depth;
		public GUIShaderRenderMessage( GUIDepth d ) {
			this.depth = d;
		}
	}
	
	private static Matrix4f matrixBuffer = new Matrix4f();
	
    public static GUIShader GLOBAL;
    public static void init() {
        GLOBAL = new GUIShader();
    }

    private GUIShader() {
        super( "glsl/gui/vertex.glsl", "glsl/gui/fragment.glsl" );
    }

    @Override
    protected void setupUniformVariables() {
		this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
		this.createUniformVariable( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX );
		this.createUniformVariable( UniformVariable.COLOR );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION_2D );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    }
    
    public void use() {
    	this.useProgram();
    	this.loadProjectionMatrix();
    }
    
    private void loadProjectionMatrix() {
    	Matrix4f.setIdentity( matrixBuffer );
    	MatrixUtils.addTranslationToMatrix( matrixBuffer, new Vector3fl(-1f,+1f));
    	MatrixUtils.addScaleToMatrix( matrixBuffer, new Vector3fl(2f,-2f));
    	MatrixUtils.addScaleToMatrix(matrixBuffer, new Vector3fl(
    		1f / Config.GAME_WIDTH,
    		1f/ Config.GAME_HEIGHT
    	));
    	this.loadMatrix4f( UniformVariable.PROJECTION_MATRIX, matrixBuffer );
    }
    
}
