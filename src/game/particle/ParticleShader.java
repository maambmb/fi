package game.particle;

import game.gfx.AttributeVariable;
import game.gfx.Batcher;
import game.gfx.BufferType;
import game.gfx.Model;
import game.gfx.Shader;
import game.gfx.TextureRef;
import game.gfx.UniformVariable;

public final class ParticleShader extends Shader {

    public static class ParticleShaderPreRenderMessage { }

    public static ParticleShader GLOBAL;
    public static void setup() {
        GLOBAL = new ParticleShader();
    }
    
    public Batcher batcher;
    private Model batchModel;
    
    private ParticleShader() {
        super( "glsl/particle/vertex.glsl", "glsl/particle/fragment.glsl" );
        this.batcher = new Batcher();
        this.batchModel = new Model( TextureRef.PARTICLE, this.getUsedAttributeVariables(), BufferType.STREAM );
    }

    @Override
    protected void setupUniformVariables() {
    	this.createUniformVariable( UniformVariable.VIEW_MATRIX );
    	this.createUniformVariable( UniformVariable.VIEW_UNROTATOR );
    	this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
    }

    @Override
    protected void setupAttributeVariables() {
    	this.createAttributeVariable( AttributeVariable.POSITION );
    	this.createAttributeVariable( AttributeVariable.POSITION_WORLD );
    	this.createAttributeVariable( AttributeVariable.TEX_COORDS );
    	this.createAttributeVariable( AttributeVariable.COLOR );
    }
    
    public void render() {
    	this.batcher.render( this.batchModel );
    }
    

}
