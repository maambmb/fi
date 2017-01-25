package game.component;

import game.Entity;
import game.gfx.shader.BlockShader;

public class ModelBlockRenderComponent extends ModelRenderComponent {

    @Override
    public void setup( Entity e ) {
        super.setup( e );
        e.globalListenerClient.addListener( BlockShader.BlockShaderRenderMessage.class, 
            (msg) -> this.shaderRender( BlockShader.SHADER ) );
    }

}
