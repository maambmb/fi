package game.block;

import game.Component;
import game.Entity;
import game.Position3DComponent;
import game.block.BlockShader.BlockShaderRenderMessage;
import game.gfx.Model;
import game.gfx.UniformVariable;
import util.Matrix4fl;

public class BlockRenderComponent implements Component {

	public Model model;
	private Position3DComponent posCmpt;
	private static Matrix4fl matrix = new Matrix4fl();
	
    @Override
    public void setup( Entity e ) {
        // only render when the block shader program is active
        e.listener.addSubscriber( BlockShader.BlockShaderRenderMessage.class, this::render );
        e.listener.addSubscriber( Position3DComponent.class, x -> this.posCmpt = x );
    }
    
    private void render( BlockShaderRenderMessage msg ) {

    	if( this.model == null)
    		return;

    	matrix.clearMatrix();
    	matrix.addTranslationToMatrix( this.posCmpt.position );
    	matrix.addScaleToMatrix( this.posCmpt.scale );
    	BlockShader.GLOBAL.loadMatrix4f( UniformVariable.MODEL, matrix );

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
