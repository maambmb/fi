package game.gfx.shader;

import game.block.LightSource;
import game.gfx.AttributeVariable;
import game.gfx.UniformVariable;

public final class BlockShader extends Shader {

    public static class BlockShaderPrepareMessage { }
    public static class BlockShaderRenderMessage { }

    private static UniformVariable[] USED_UNIFORM_VARS = {
        UniformVariable.MODEL_ROTATE_MATRIX,
        UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX,
        UniformVariable.VIEW_MATRIX,
        UniformVariable.PROJECTION_MATRIX,
        UniformVariable.LIGHTING_BASE,
        UniformVariable.FOG_COLOR,
        UniformVariable.GLOBAL_LIGHT_ORIGIN,
    };

    public static AttributeVariable[] USED_ATTRIBUTE_VARS = {
        AttributeVariable.POSITION,
        AttributeVariable.TEX_COORDS,
        AttributeVariable.NORMAL,
        AttributeVariable.SHADOW,
        AttributeVariable.LIGHTING_CONSTANT,
        AttributeVariable.LIGHTING_GLOBAL,
    };

    public static BlockShader SHADER;
    public static void init() {
        SHADER = new BlockShader();
    }

    private BlockShader() {
        super( "glsl/block/vertex.glsl", "glsl/block/fragment.glsl" );
        this.setup();
    }

    @Override
    protected void setupUniformVariables() {
        for( UniformVariable uv : USED_UNIFORM_VARS )
            this.createUniformVariable( uv );
        for( LightSource ls : LightSource.values() )
            this.createUniformVariable( ls.uniformVariable );
    }

    @Override
    protected void setupAttributeVariables() {
        for( AttributeVariable av : USED_ATTRIBUTE_VARS ) 
            this.createAttributeVariable( av );
        for( LightSource ls : LightSource.values() )
            this.createAttributeVariable( ls.attributeVariable );
    }

}
