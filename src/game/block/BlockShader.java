package game.block;

import game.gfx.AttributeVariable;
import game.gfx.Shader;
import game.gfx.UniformVariable;

public final class BlockShader extends Shader {

    public static class BlockShaderPreRenderMessage { }
    public static class BlockShaderRenderMessage { }

    public static BlockShader GLOBAL;
    public static void init() {
        GLOBAL = new BlockShader();
    }
    
    private BlockShader() {
        super( "glsl/block/vertex.glsl", "glsl/block/fragment.glsl" );
    }

    @Override
    protected void setupUniformVariables() {
    	this.createUniformVariable( UniformVariable.MODEL );
    	this.createUniformVariable( UniformVariable.VIEW_MATRIX );
    	this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
    	this.createUniformVariable( UniformVariable.LIGHTING_BASE );
    	this.createUniformVariable( UniformVariable.FOG_COLOR );
    	this.createUniformVariable( UniformVariable.LIGHT_ORIGIN );
    	this.createUniformVariable( UniformVariable.LIGHTING_CONSTANT );
    	this.createUniformVariable( UniformVariable.LIGHTING_GLOBAL );
    	this.createUniformVariable( UniformVariable.MAX_DISTANCE );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    	this.createAttributeVariable( AttributeVariable.NORMAL );
    	this.createAttributeVariable( AttributeVariable.SHADOW );
    	this.createAttributeVariable( AttributeVariable.LIGHTING_CONSTANT );
    	this.createAttributeVariable( AttributeVariable.LIGHTING_GLOBAL );
    }
    

}
