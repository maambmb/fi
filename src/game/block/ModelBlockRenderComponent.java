package game.block;

import game.Entity;
import game.Position3DComponent;
import game.block.BlockShader.BlockShaderRenderMessage;
import game.gfx.ModelRenderComponent;

public class ModelBlockRenderComponent extends ModelRenderComponent {

	private Position3DComponent posCmpt;
	
    @Override
    public void setup( Entity e ) {
        super.setup( e );
        // only render when the block shader program is active
        e.listener.addSubscriber( BlockShader.BlockShaderRenderMessage.class, this::render );
        e.listener.addSubscriber( Position3DComponent.class, x -> this.posCmpt = x );
    }
    
    private void render( BlockShaderRenderMessage msg ) {

    	if( this.model == null)
    		return;

    	this.loadRotateModelMatrix( BlockShader.GLOBAL, this.posCmpt.rotation );
    	this.loadTranslateScaleMatrix( BlockShader.GLOBAL, this.posCmpt.position, this.posCmpt.scale );
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

}
