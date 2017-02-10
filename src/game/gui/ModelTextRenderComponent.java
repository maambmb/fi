package game.gui;

import game.Entity;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.ModelRenderComponent;
import game.gui.GUIShader.GUIShaderRenderMessage;
import util.Vector3fl;

public class ModelTextRenderComponent extends ModelRenderComponent {

	
	public FontMap fontMap;
	public float fontSize;
	public Vector3fl position;
	
	public ModelTextRenderComponent() {
		this.fontMap = FontMap.DEBUG;
		this.fontSize = 0.02f;
		this.position = new Vector3fl();
	}
	
    @Override
    public void setup( Entity e ) {
        super.setup( e );
        // only render when the block shader program is active
        e.listener.addSubscriber( GUIShader.GUIShaderRenderMessage.class, this::render );
    }
    
    private void render( GUIShaderRenderMessage msg ) {
    	if( this.model == null )
    		return;

    	this.loadTranslateScaleMatrix( GUIShader.GLOBAL, this.position, 1f );
    	this.model.render();
    }
    
    public void setText( String txt ) {

    	this.destroy();
    	this.model = new Model();
    	this.model.texture = this.fontMap.atlas;

    	char[] charArray = txt.toCharArray();
    	float fontHeight = this.fontSize * this.fontMap.charDimensions.y / this.fontMap.charDimensions.x;
    	
    	for( int i = 0; i < charArray.length; i +=1 ) {

			Vector3fl baseTex = this.fontMap.getPosition( charArray[i] );

    		for( int j = 0; j < 4; j += 1 ) {

    			boolean farHorizontal = ( j & 0x01 ) > 0;
    			boolean farVertical   = ( j & 0x02 ) > 0;
    			
    			this.model.addAttributeData2D( AttributeVariable.POSITION_2D, new Vector3fl(
    				i * this.fontSize + ( farHorizontal ? this.fontSize : 0 ),
    				farVertical ? fontHeight : 0 
    			) );
    			
    			this.model.addAttributeData2D( AttributeVariable.TEX_COORDS, new Vector3fl(
    				farHorizontal ? this.fontMap.charDimensions.x : 0f,
    				farVertical ? this.fontMap.charDimensions.y : 0f
    			).add( baseTex ) );

    			
    		}
    		this.model.addQuad();
    	}
    	
    	this.model.buildModel();
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
