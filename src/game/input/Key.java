package game.input;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

public enum Key {
	
	NO_KEY( -1 ),
	
	KEY_A( Keyboard.KEY_A, 'a', 'A'),
	KEY_B( Keyboard.KEY_B, 'b', 'B'),
	KEY_C( Keyboard.KEY_C, 'c', 'C'),
	KEY_D( Keyboard.KEY_D, 'd', 'D'),
	KEY_E( Keyboard.KEY_E, 'e', 'E'),
	KEY_F( Keyboard.KEY_F, 'f', 'F'),
	KEY_G( Keyboard.KEY_G, 'g', 'G'),
	KEY_H( Keyboard.KEY_H, 'h', 'H'),
	KEY_I( Keyboard.KEY_I, 'i', 'I'),
	KEY_J( Keyboard.KEY_J, 'j', 'J'),
	KEY_K( Keyboard.KEY_K, 'k', 'K'),
	KEY_L( Keyboard.KEY_L, 'l', 'L'),
	KEY_M( Keyboard.KEY_M, 'm', 'M'),
	KEY_N( Keyboard.KEY_N, 'n', 'N'),
	KEY_O( Keyboard.KEY_O, 'o', 'O'),
	KEY_P( Keyboard.KEY_P, 'p', 'P'),
	KEY_Q( Keyboard.KEY_Q, 'q', 'Q'),
	KEY_R( Keyboard.KEY_R, 'r', 'R'),
	KEY_S( Keyboard.KEY_S, 's', 'S'),
	KEY_T( Keyboard.KEY_T, 't', 'T'),
	KEY_U( Keyboard.KEY_U, 'u', 'U'),
	KEY_V( Keyboard.KEY_V, 'v', 'V'),
	KEY_W( Keyboard.KEY_W, 'w', 'W'),
	KEY_X( Keyboard.KEY_X, 'x', 'X'),
	KEY_Y( Keyboard.KEY_Y, 'y', 'Y'),
	KEY_Z( Keyboard.KEY_Z, 'z', 'Z'),

	KEY_1( Keyboard.KEY_0, '1', '!'),
	KEY_2( Keyboard.KEY_0, '2', '@'),
	KEY_3( Keyboard.KEY_0, '3' ),
	KEY_4( Keyboard.KEY_0, '4' ),
	KEY_5( Keyboard.KEY_0, '5' ),
	KEY_6( Keyboard.KEY_0, '6' ),
	KEY_7( Keyboard.KEY_0, '7' ),
	KEY_8( Keyboard.KEY_0, '8', '*'),
	KEY_9( Keyboard.KEY_0, '9', '('),
	KEY_0( Keyboard.KEY_0, '0', ')'),
	
	KEY_LBRACKET( Keyboard.KEY_LBRACKET, '[', '{' ),
	KEY_RBRACKET( Keyboard.KEY_RBRACKET, ']', '}' ),
	KEY_COLON( Keyboard.KEY_COLON, ';', ':' ),
	KEY_PERIOD( Keyboard.KEY_PERIOD, '.', '>' ),
	KEY_COMMA( Keyboard.KEY_COMMA, ',', '<' ),
	KEY_SUBTRACT( Keyboard.KEY_SUBTRACT, '-', '_' ),
	KEY_EQUALS( Keyboard.KEY_EQUALS, '=', '+' ),
	KEY_SPACE( Keyboard.KEY_SPACE, ' ' ),
	KEY_SLASH( Keyboard.KEY_SLASH, '/', '?' ),
	KEY_GRAVE( Keyboard.KEY_GRAVE, '`' ),

	KEY_LSHIFT( Keyboard.KEY_LSHIFT ),
	KEY_LCONTROL( Keyboard.KEY_LCONTROL ),
	KEY_RETURN( Keyboard.KEY_RETURN ),
	KEY_ESCAPE( Keyboard.KEY_ESCAPE ),
	KEY_UP( Keyboard.KEY_UP ),
	KEY_DOWN( Keyboard.KEY_DOWN ),
	KEY_LEFT( Keyboard.KEY_LEFT ),
	KEY_RIGHT( Keyboard.KEY_RIGHT );

	private static Map<Integer,Key> lookup;
	public static char DEFAULT_CHAR;

	public static void init() {
		lookup = new HashMap<Integer,Key>();
		for( Key k : Key.values())
			lookup.put( k.keyCode, k );
	}
	
	public static Key fromKeyCode( int k ) {
		return lookup.getOrDefault( k, Key.NO_KEY );
	}
	
	public int keyCode;
	public char lowerCharacter;
	public char upperCharacter;
	
	private Key( int keyCode ) {
		this.keyCode = keyCode;
	}
	
	private Key( int keyCode, char lowerCharacter ) {
		this( keyCode );
		this.lowerCharacter = lowerCharacter;
	}
	
	private Key( int keyCode, char lowerCharacter, char upperCharacter ) {
		this( keyCode, lowerCharacter );
		this.upperCharacter = upperCharacter;
	}
	
}
