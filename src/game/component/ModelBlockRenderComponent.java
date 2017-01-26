package game.component;

import game.Entity;
import game.gfx.shader.BlockShader;

public class ModelBlockRenderComponent extends ModelRenderComponent {

    @Override
    public void setup( Entity e ) {
        super.setup( e );
        // only render when the block shader program is active
        e.globalListenerClient.addListener( BlockShader.BlockShaderRenderMessage.class, 
            (msg) -> this.shaderRender( BlockShader.SHADER ) );
    }

    @Override
    // block renderers are unique in that each component *owns* its model
    // as each chunk has a unique model. Thus when this component is destroyed
    // it should destroy the model (and free the VRAM) along with it.
    public void destroy() {
        this.model.destroy();
    }

}
