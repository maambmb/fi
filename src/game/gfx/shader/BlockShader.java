package game.gfx.shader;

import game.block.LightSource;
import game.gfx.AttributeVariable;
import game.gfx.UniformVariable;

public class BlockShader extends Shader {

    public static BlockShader SHADER;
    public static void init() {
        SHADER = new BlockShader();
    }

    public class BlockShaderPrepareMessage { }
    public class BlockShaderRenderMessage { }

    private final String VERTEX_PATH   = "";
    private final String FRAGMENT_PATH = "";
	
	private BlockShader() {
		super();
		this.setup( VERTEX_PATH, FRAGMENT_PATH );
	}
	
    @Override
	protected void setupUniformVariables() {
        this.createUniformVariable( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX );
        this.createUniformVariable( UniformVariable.MODEL_ROTATE_MATRIX );
        this.createUniformVariable( UniformVariable.VIEW_MATRIX );
        this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
		for( LightSource ls : LightSource.values() )
            this.createUniformVariable( ls.uniformVariable );
	}
	
    @Override
	protected void setupVAOAttributes() {
        this.createAttributeVariable( AttributeVariable.NORMAL );
        this.createAttributeVariable( AttributeVariable.SHADOW );
        this.createAttributeVariable( AttributeVariable.BLOCK_TYPE );
		for( LightSource ls : LightSource.values() )
            this.createAttributeVariable( ls.attributeVariable );
	}


}
