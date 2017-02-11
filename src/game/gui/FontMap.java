package game.gui;

import java.util.HashMap;
import java.util.Map;

import game.Config;
import game.Game.DestroyMessage;
import game.Listener;
import game.gfx.AttributeVariable;
import game.gfx.Model;
import game.gfx.TextureRef;
import util.Vector3fl;
import util.Vector3in;

public enum FontMap {
	
	DEBUG( 
		TextureRef.FONT_DEBUG, 
		new Vector3in(7,9,0),
		new Glyph[] {
				Glyph.SPACE, Glyph.EXCLAMATION, Glyph.DQUOTE, Glyph.HASH, Glyph.DOLLAR, Glyph.PERCENT,
				Glyph.AMPERSAND, Glyph.SQUOTE, Glyph.LPARENS, Glyph.RPARENS, Glyph.ASTERIX, Glyph.PLUS,
				Glyph.COMMA, Glyph.DASH, Glyph.PERIOD, Glyph.SLASH, Glyph.NUM_0, Glyph.NUM_1,
				Glyph.NUM_2, Glyph.NUM_3, Glyph.NUM_4, Glyph.NUM_5, Glyph.NUM_6, Glyph.NUM_7,
				Glyph.NUM_8, Glyph.NUM_9, Glyph.COLON, Glyph.SEMICOLON, Glyph.LANGLE, Glyph.EQUALS, Glyph.RANGLE,
				Glyph.QUESTION, Glyph.AT, Glyph.UPPER_A, Glyph.UPPER_B, Glyph.UPPER_C, Glyph.UPPER_D, Glyph.UPPER_E,
				Glyph.UPPER_F, Glyph.UPPER_G, Glyph.UPPER_H, Glyph.UPPER_I, Glyph.UPPER_J, Glyph.UPPER_K, Glyph.UPPER_L,
				Glyph.UPPER_M, Glyph.UPPER_N, Glyph.UPPER_O, Glyph.UPPER_P, Glyph.UPPER_Q, Glyph.UPPER_R, Glyph.UPPER_S,
				Glyph.UPPER_T, Glyph.UPPER_U, Glyph.UPPER_V, Glyph.UPPER_W, Glyph.UPPER_X, Glyph.UPPER_Y, Glyph.UPPER_Z,
				Glyph.LSQUARE, Glyph.BSLASH, Glyph.RSQUARE, Glyph.HAT, Glyph.UNDERSCORE, Glyph.GRAVE, 
				Glyph.LOWER_A, Glyph.LOWER_B, Glyph.LOWER_C, Glyph.LOWER_D, Glyph.LOWER_E,
				Glyph.LOWER_F, Glyph.LOWER_G, Glyph.LOWER_H, Glyph.LOWER_I, Glyph.LOWER_J, Glyph.LOWER_K, Glyph.LOWER_L,
				Glyph.LOWER_M, Glyph.LOWER_N, Glyph.LOWER_O, Glyph.LOWER_P, Glyph.LOWER_Q, Glyph.LOWER_R, Glyph.LOWER_S,
				Glyph.LOWER_T, Glyph.LOWER_U, Glyph.LOWER_V, Glyph.LOWER_W, Glyph.LOWER_X, Glyph.LOWER_Y, Glyph.LOWER_Z,
				Glyph.LBRACE, Glyph.PIPE, Glyph.RBRACE, Glyph.TILDE, Glyph.SCANLINES, Glyph.BLOCK
		}
	);
	
	public static void init() {
		for( FontMap fm : FontMap.values() ) {
			for( int i = 0; i < fm.glyphs.length; i += 1 )
				fm.glyphLookup.put( fm.glyphs[i], fm.buildModel( i ) );
		}
	}
	
	public Vector3in dimensions;
	private TextureRef atlas;
	private Glyph[] glyphs;

	private Map<Glyph,Model> glyphLookup;
	
	private FontMap( TextureRef atlas, Vector3in charDimensions, Glyph[] glyphs ) {
		this.atlas = atlas;
		this.dimensions = charDimensions;
		this.glyphs = glyphs;
		this.glyphLookup = new HashMap<Glyph,Model>();
		Listener.GLOBAL.addSubscriber( DestroyMessage.class, this::destroy );
	}
		
	public Model getModel( Glyph g ) {
		if( ! this.glyphLookup.containsKey( g ) )
			return this.glyphLookup.get( Glyph.SCANLINES );
		return this.glyphLookup.get( g );
	}
	
	private void destroy( DestroyMessage msg ) {
		for( Model m : this.glyphLookup.values() )
			m.destroy();
	}

	private Model buildModel( int ix ) {

		Model model = new Model();
		model.texture = this.atlas;

		int stripCount = this.atlas.size / this.dimensions.x;
		Vector3fl scaledDims = this.dimensions.toVector3fl().divide( this.atlas.size );
		Vector3fl baseTex = new Vector3fl( 
			( ix % stripCount ) * scaledDims.x,
			( ix / stripCount ) * scaledDims.y
		);
		
		for( int j = 0; j < 4; j += 1 ) {

			boolean farHorizontal = ( j & 0x01 ) > 0;
			boolean farVertical   = ( j & 0x02 ) > 0;
			
			model.addAttributeData2D( AttributeVariable.POSITION_2D, new Vector3fl(
				( farHorizontal ? this.dimensions.x : 0 ),
				( farVertical ?  this.dimensions.y : 0 ) 
			) );
			
			model.addAttributeData2D( AttributeVariable.TEX_COORDS, new Vector3fl(
				farHorizontal ? (float) scaledDims.x : 0f,
				farVertical ? (float) scaledDims.y : 0f
			).add( baseTex ) );
			
		}

		model.addQuad();
		model.buildModel();
		return model;

	}
}
