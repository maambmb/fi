package game.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import game.gui.Glyph;

public enum Key {
	
	KEY_A( Keyboard.KEY_A, Glyph.LOWER_A, Glyph.UPPER_A ),
	KEY_B( Keyboard.KEY_B, Glyph.LOWER_B, Glyph.UPPER_B ),
	KEY_C( Keyboard.KEY_C, Glyph.LOWER_C, Glyph.UPPER_C ),
	KEY_D( Keyboard.KEY_D, Glyph.LOWER_D, Glyph.UPPER_D ),
	KEY_E( Keyboard.KEY_E, Glyph.LOWER_E, Glyph.UPPER_E ),
	KEY_F( Keyboard.KEY_F, Glyph.LOWER_F, Glyph.UPPER_F ),
	KEY_G( Keyboard.KEY_G, Glyph.LOWER_G, Glyph.UPPER_G ),
	KEY_H( Keyboard.KEY_H, Glyph.LOWER_H, Glyph.UPPER_H ),
	KEY_I( Keyboard.KEY_I, Glyph.LOWER_I, Glyph.UPPER_I ),
	KEY_J( Keyboard.KEY_J, Glyph.LOWER_J, Glyph.UPPER_J ),
	KEY_K( Keyboard.KEY_K, Glyph.LOWER_K, Glyph.UPPER_K ),
	KEY_L( Keyboard.KEY_L, Glyph.LOWER_L, Glyph.UPPER_L ),
	KEY_M( Keyboard.KEY_M, Glyph.LOWER_M, Glyph.UPPER_M ),
	KEY_N( Keyboard.KEY_N, Glyph.LOWER_N, Glyph.UPPER_N ),
	KEY_O( Keyboard.KEY_O, Glyph.LOWER_O, Glyph.UPPER_O ),
	KEY_P( Keyboard.KEY_P, Glyph.LOWER_P, Glyph.UPPER_P ),
	KEY_Q( Keyboard.KEY_Q, Glyph.LOWER_Q, Glyph.UPPER_Q ),
	KEY_R( Keyboard.KEY_R, Glyph.LOWER_R, Glyph.UPPER_R ),
	KEY_S( Keyboard.KEY_S, Glyph.LOWER_S, Glyph.UPPER_S ),
	KEY_T( Keyboard.KEY_T, Glyph.LOWER_T, Glyph.UPPER_T ),
	KEY_U( Keyboard.KEY_U, Glyph.LOWER_U, Glyph.UPPER_U ),
	KEY_V( Keyboard.KEY_V, Glyph.LOWER_V, Glyph.UPPER_V ),
	KEY_W( Keyboard.KEY_W, Glyph.LOWER_W, Glyph.UPPER_W ),
	KEY_X( Keyboard.KEY_X, Glyph.LOWER_X, Glyph.UPPER_X ),
	KEY_Y( Keyboard.KEY_Y, Glyph.LOWER_Y, Glyph.UPPER_Y ),
	KEY_Z( Keyboard.KEY_Z, Glyph.LOWER_Z, Glyph.UPPER_Z ),

	KEY_0( Keyboard.KEY_0, Glyph.NUM_0, Glyph.RPARENS ),
	KEY_1( Keyboard.KEY_1, Glyph.NUM_1, Glyph.EXCLAMATION ),
	KEY_2( Keyboard.KEY_2, Glyph.NUM_2, Glyph.DQUOTE ),
	KEY_3( Keyboard.KEY_3, Glyph.NUM_3, Glyph.DOLLAR ),
	KEY_4( Keyboard.KEY_4, Glyph.NUM_4, Glyph.DOLLAR ),
	KEY_5( Keyboard.KEY_5, Glyph.NUM_5, Glyph.PERCENT ),
	KEY_6( Keyboard.KEY_6, Glyph.NUM_6, Glyph.HAT ),
	KEY_7( Keyboard.KEY_7, Glyph.NUM_7, Glyph.AMPERSAND ),
	KEY_8( Keyboard.KEY_8, Glyph.NUM_8, Glyph.ASTERIX ),
	KEY_9( Keyboard.KEY_9, Glyph.NUM_9, Glyph.LPARENS ),
	
	KEY_LBRACKET( Keyboard.KEY_LBRACKET, Glyph.LSQUARE, Glyph.LBRACE ),
	KEY_RBRACKET( Keyboard.KEY_RBRACKET, Glyph.RSQUARE, Glyph.RBRACE ),
	KEY_COLON( Keyboard.KEY_COLON, Glyph.SEMICOLON, Glyph.COLON ),
	KEY_PERIOD( Keyboard.KEY_PERIOD, Glyph.PERIOD, Glyph.RANGLE ),
	KEY_COMMA( Keyboard.KEY_COMMA, Glyph.COMMA, Glyph.LANGLE ),
	KEY_DASH( Keyboard.KEY_MINUS, Glyph.DASH, Glyph.UNDERSCORE ),
	KEY_EQUALS( Keyboard.KEY_EQUALS, Glyph.EQUALS, Glyph.PLUS ),
	KEY_SPACE( Keyboard.KEY_SPACE, Glyph.SPACE, Glyph.SPACE ),
	KEY_SLASH( Keyboard.KEY_SLASH, Glyph.SLASH, Glyph.QUESTION ),
	KEY_GRAVE( Keyboard.KEY_GRAVE, Glyph.GRAVE ),
	KEY_QUOTE( Keyboard.KEY_APOSTROPHE, Glyph.SQUOTE, Glyph.AT ),

	KEY_HAT( Keyboard.KEY_CIRCUMFLEX, Glyph.HAT, Glyph.HAT ),
	KEY_UNDERSCORE( Keyboard.KEY_UNDERLINE, Glyph.UNDERSCORE, Glyph.UNDERSCORE),
	KEY_AT( Keyboard.KEY_AT, Glyph.AT, Glyph.AT ),

	KEY_LSHIFT( Keyboard.KEY_LSHIFT ),
	KEY_LCONTROL( Keyboard.KEY_LCONTROL ),
	KEY_RETURN( Keyboard.KEY_RETURN ),
	KEY_ESCAPE( Keyboard.KEY_ESCAPE ),
	KEY_UP( Keyboard.KEY_UP ),
	KEY_DOWN( Keyboard.KEY_DOWN ),
	KEY_LEFT( Keyboard.KEY_LEFT ),
	KEY_RIGHT( Keyboard.KEY_RIGHT ),
	KEY_BACKSPACE( Keyboard.KEY_BACK );

	private static Map<Integer,Key> lookup;

	public static void init() {
		lookup = new HashMap<Integer,Key>();
		for( Key k : Key.values())
			lookup.put( k.keyCode, k );
	}
	
	public int keyCode;
	public Glyph lowerGlyph;
	public Glyph upperGlyph;
	public boolean upperAlias;
	
	private Key( int keyCode ) {
		this.keyCode = keyCode;
	}
	
	private Key( int keyCode, Glyph lowerGlyph ) {
		this( keyCode );
		this.lowerGlyph = lowerGlyph;
	}
	
	private Key( int keyCode, Glyph lowerGlyph, Glyph upperGlyph ) {
		this( keyCode, lowerGlyph );
		this.upperGlyph = upperGlyph;
	}

	private Key( int keyCode, Glyph lowerGlyph, Glyph upperGlyph, boolean upperAlias ) {
		this( keyCode, lowerGlyph );
		this.upperGlyph = upperGlyph;
		this.upperAlias = upperAlias;
	}
	
}
