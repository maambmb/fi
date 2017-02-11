package game.block;

import org.lwjgl.util.vector.Matrix4f;

import game.Config;
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
    
    private static Matrix4f matrixBuffer = new Matrix4f();

    private BlockShader() {
        super( "glsl/block/vertex.glsl", "glsl/block/fragment.glsl" );
    }

    private void loadProjectionMatrix() {

        float yScale = (float) (Config.ASPECT_RATIO / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW / 2f )));
        float xScale = yScale / Config.ASPECT_RATIO;
        float frustrumLength = Config.FAR_PLANE - Config.NEAR_PLANE;

        Matrix4f.setIdentity(matrixBuffer);

        matrixBuffer.m00 = xScale;
        matrixBuffer.m11 = yScale;
        matrixBuffer.m22 = - (Config.FAR_PLANE + Config.NEAR_PLANE) / frustrumLength;
        matrixBuffer.m23 = -1;
        matrixBuffer.m32 = -2 * Config.NEAR_PLANE * Config.FAR_PLANE / frustrumLength;
        matrixBuffer.m33 = 0;

        this.loadMatrix4f( UniformVariable.PROJECTION_MATRIX, matrixBuffer );
    }
    
    @Override
    public void use() {
    	super.use();
    	this.loadProjectionMatrix();
    }

    @Override
    protected void setupUniformVariables() {
    	this.createUniformVariable( UniformVariable.MODEL_ROTATE_MATRIX );
    	this.createUniformVariable( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX );
    	this.createUniformVariable( UniformVariable.VIEW_MATRIX );
    	this.createUniformVariable( UniformVariable.PROJECTION_MATRIX );
    	this.createUniformVariable( UniformVariable.LIGHTING_BASE );
    	this.createUniformVariable( UniformVariable.FOG_COLOR );
    	this.createUniformVariable( UniformVariable.GLOBAL_LIGHT_ORIGIN );
    	this.createUniformVariable( UniformVariable.LIGHTING_CONSTANT );
    	this.createUniformVariable( UniformVariable.LIGHTING_GLOBAL );
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
