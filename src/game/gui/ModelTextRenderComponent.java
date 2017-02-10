package game.gui;

import java.util.ArrayList;
import java.util.List;

import game.Entity;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.ModelRenderComponent;
import game.gui.GUIShader.GUIShaderRenderMessage;
import util.Tuple;
import util.Vector3fl;
import util.Vector3in;

public class ModelTextRenderComponent extends ModelRenderComponent {

	
	public FontMap fontMap;
	public float fontSize;
	public Vector3fl position;
	private List<Tuple.Binary<Character,Vector3in>> characterList;
	
	public ModelTextRenderComponent() {
		this.fontMap = FontMap.DEBUG;
		this.fontSize = 0.02f;
		this.position = new Vector3fl();
		this.characterList = new ArrayList<Tuple.Binary<Character,Vector3in>>();
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
    
    public void clearText() {
    	this.characterList.clear();
    }
    
    public void addString( String s ) {
    	this.addString( s, new Vector3in( 0xFFFFFF ) );
    }
    
    public void addString( String s, Vector3in color ) {
    	for( char c : s.toCharArray() )
			this.characterList.add( Tuple.create( c, color ) );
    }
    
    public void rebuild() {

    	this.destroy();
    	this.model = new Model();
    	this.model.texture = this.fontMap.atlas;

    	float fontHeight = this.fontSize * this.fontMap.charDimensions.y / this.fontMap.charDimensions.x;
    	for( int i = 0; i < this.characterList.size(); i +=1 ) {

    		Tuple.Binary<Character,Vector3in> coloredChar = this.characterList.get( i );
			Vector3fl baseTex = this.fontMap.getPosition( coloredChar.arg1 );

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
    			
    			this.model.addAttributeData( AttributeVariable.COLOR, coloredChar.arg2.toPackedBytes() );
    			
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
