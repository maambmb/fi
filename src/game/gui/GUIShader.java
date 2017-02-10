package game.gui;

import game.gfx.AttributeVariable;
import game.gfx.Shader;
import game.gfx.UniformVariable;

public class GUIShader extends Shader {

	public static class GUIShaderRenderMessage {
		public GUIDepth depth;
		public GUIShaderRenderMessage( GUIDepth d ) {
			this.depth = d;
		}
	}
	
    public static GUIShader GLOBAL;
    public static void init() {
        GLOBAL = new GUIShader();
    }

    private GUIShader() {
        super( "glsl/gui/vertex.glsl", "glsl/gui/fragment.glsl" );
    }

    @Override
    protected void setupUniformVariables() {
		this.createUniformVariable( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX );
		this.createUniformVariable( UniformVariable.COLOR );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION_2D );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    }
    
}
