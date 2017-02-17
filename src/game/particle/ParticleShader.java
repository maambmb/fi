package game.particle;

import game.gfx.AttributeVariable;
import game.gfx.Shader;
import game.gfx.UniformVariable;

public final class ParticleShader extends Shader {

    public static class BlockShaderPreRenderMessage { }
    public static class BlockShaderRenderMessage { }

    public static ParticleShader GLOBAL;
    public static void setup() {
        GLOBAL = new ParticleShader();
    }
    
    private ParticleShader() {
        super( "glsl/block/vertex.glsl", "glsl/block/fragment.glsl" );
    }

    @Override
    protected void setupUniformVariables() {
    	this.createUniformVariable( UniformVariable.MODEL );
    	this.createUniformVariable( UniformVariable.VIEW_MATRIX );
    	this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
    	this.createUniformVariable( UniformVariable.TEXTURE_OFFSET );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    }
    

}
