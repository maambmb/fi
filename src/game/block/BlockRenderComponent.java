package game.block;

import org.lwjgl.util.vector.Matrix4f;

import game.Component;
import game.Entity;
import game.Position3DComponent;
import game.block.BlockShader.BlockShaderRenderMessage;
import game.gfx.Model;
import game.gfx.UniformVariable;
import util.MatrixUtils;

public class BlockRenderComponent implements Component {

	public Model model;
	private Position3DComponent posCmpt;
	private static Matrix4f matrixBuffer = new Matrix4f();
	
    @Override
    public void setup( Entity e ) {
        // only render when the block shader program is active
        e.listener.addSubscriber( BlockShader.BlockShaderRenderMessage.class, this::render );
        e.listener.addSubscriber( Position3DComponent.class, x -> this.posCmpt = x );
    }
    
    private void render( BlockShaderRenderMessage msg ) {

    	if( this.model == null)
    		return;

    	matrixBuffer.setIdentity();
    	MatrixUtils.addRotationMatrixReversed( matrixBuffer, this.posCmpt.rotation );
    	BlockShader.GLOBAL.loadMatrix4f( UniformVariable.MODEL_ROTATE_MATRIX , matrixBuffer );
    	
    	matrixBuffer.setIdentity();
    	MatrixUtils.addTranslationToMatrix( matrixBuffer, this.posCmpt.position );
    	MatrixUtils.addScaleToMatrix( matrixBuffer, this.posCmpt.scale );
    	BlockShader.GLOBAL.loadMatrix4f( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX, matrixBuffer );

    	this.model.render();
    }

    @Override
    // block renderers are unique in that each component *owns* its model
    // as each chunk has a unique model. Thus when this component is destroyed
    // it should destroy the model (and free the VRAM) along with it.
    public void destroy() {
        if( this.model != null )
            this.model.destroy();
    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
