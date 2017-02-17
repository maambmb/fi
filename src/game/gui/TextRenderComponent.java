package game.gui;

import java.util.ArrayList;
import java.util.List;

import game.Component;
import game.Entity;
import game.gfx.AttributeVariable;
import game.gfx.BatchElement;
import game.gfx.Model;
import game.gfx.TextureRef;
import game.gui.GUIShader.GUIShaderPreRenderMessage;
import util.Tuple;
import util.Vector3fl;
import util.Vector3in;

public class TextRenderComponent implements Component, BatchElement {

	public static class TextCharacter {
		public boolean newline;
		public Glyph glyph;
		public Vector3in color;
		public TextCharacter() {
			this.newline = true;
		}
		
		public TextCharacter( Glyph g ) {
			this( g, Vector3in.WHITE );
		}
		
		public TextCharacter( Glyph g , Vector3in color ) {
			this.glyph = g;
			this.color = color;
		}
	}
	
	public float fontSize;
	public int depth;
	public boolean visible;
	public Vector3fl position;
	private List<TextCharacter> characters;

	public TextRenderComponent( Entity e ) {
		this.depth = 0;
		this.fontSize = 2f;
		this.position = new Vector3fl();
		this.characters = new ArrayList<TextCharacter>();
        e.listener.addSubscriber( GUIShader.GUIShaderPreRenderMessage.class, this::preRender );
	}
	
    private void preRender( GUIShaderPreRenderMessage msg ) {
    	if(this.visible)
			GUIShader.GLOBAL.batcher.addToBatch( this );
    }
    
    public void reset() {
    	this.characters.clear();
    }
    
    public void add( TextCharacter tc ) {
    	this.characters.add( tc );
    }
    
    public void newline() {
    	this.characters.add( new TextCharacter() );
    }
    
    public void addString( String s, Vector3in color ) {
    	for( char c : s.toCharArray() )
    		this.add( new TextCharacter( Glyph.lookup( c ), color ) );
    }

    @Override
    public void destroy() {
    }

	@Override
	public void init() {
		
	}

	@Override
	public int getDepth() {
		return this.depth;
	}

	@Override
	public void renderToBatch(Model batch) {

    	int xCur = 0;
    	int yCur = 0;

    	for( TextCharacter tc : this.characters ) {
    		
    		if( tc.newline) {
    			xCur = 0;
    			yCur += 1;
    			continue;
    		}

    		Vector3fl offset = new Vector3fl( xCur, yCur );
			for( Vector3fl v : Model.QUAD_VERTICES ) {
				Vector3fl reranged = v.multiply( 0.5f ).add( 0.5f );
				batch.addAttributeData2D( AttributeVariable.POSITION_2D, offset.add( reranged ).multiply( TextureRef.GUI.elementSize ).multiply( this.fontSize ).add( this.position ) );
				batch.addAttributeData2D( AttributeVariable.TEX_COORDS, reranged.multiply( TextureRef.GUI.elementSize ).add( tc.glyph.rawTexCoords ).divide( TextureRef.GUI.size ) );
				batch.addAttributeData( AttributeVariable.COLOR, tc.color.toPackedBytes() );
			}
			batch.addQuad();

			xCur += 1;
    	}
	}

}
