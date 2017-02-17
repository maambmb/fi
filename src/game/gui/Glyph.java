package game.gui;

import java.util.HashMap;
import java.util.Map;

import game.gfx.TextureRef;
import util.Vector3fl;

public enum Glyph {


	
	

	
	SPACE(' '),
	EXCLAMATION('!'),
	DQUOTE('"'),
	HASH('#'),
	DOLLAR('$'),
	PERCENT('%'),
	AMPERSAND('&'),
	SQUOTE('\''),
	LPARENS('('),
	RPARENS(')'),
	ASTERIX('*'),
	PLUS('+'),
	COMMA(','),
	DASH('-'),
	PERIOD('.'),
	SLASH( '/' ),

	NUM_0('0'),
	NUM_1('1'),
	NUM_2('2'),
	NUM_3('3'),
	NUM_4('4'),
	NUM_5('5'),
	NUM_6('6'),
	NUM_7('7'),
	NUM_8('8'),
	NUM_9('9'),

	COLON(':'),
	SEMICOLON(';'),
	LANGLE('<'),
	EQUALS('='),
	RANGLE('>'),
	QUESTION('?'),
	AT('@'),

	UPPER_A('A'),
	UPPER_B('B'),
	UPPER_C('C'),
	UPPER_D('D'),
	UPPER_E('E'),
	UPPER_F('F'),
	UPPER_G('G'),
	UPPER_H('H'),
	UPPER_I('I'),
	UPPER_J('J'),
	UPPER_K('K'),
	UPPER_L('L'),
	UPPER_M('M'),
	UPPER_N('N'),
	UPPER_O('O'),
	UPPER_P('P'),
	UPPER_Q('Q'),
	UPPER_R('R'),
	UPPER_S('S'),
	UPPER_T('T'),
	UPPER_U('U'),
	UPPER_V('V'),
	UPPER_W('W'),
	UPPER_X('X'),
	UPPER_Y('Y'),
	UPPER_Z('Z'),

	LSQUARE( '['),
	BSLASH('\\'),
	RSQUARE( ']' ),
	HAT('^'),
	UNDERSCORE('_'),
	GRAVE('`'),

	LOWER_A('a'),
	LOWER_B('b'),
	LOWER_C('c'),
	LOWER_D('d'),
	LOWER_E('e'),
	LOWER_F('f'),
	LOWER_G('g'),
	LOWER_H('h'),
	LOWER_I('i'),
	LOWER_J('j'),
	LOWER_K('k'),
	LOWER_L('l'),
	LOWER_M('m'),
	LOWER_N('n'),
	LOWER_O('o'),
	LOWER_P('p'),
	LOWER_Q('q'),
	LOWER_R('r'),
	LOWER_S('s'),
	LOWER_T('t'),
	LOWER_U('u'),
	LOWER_V('v'),
	LOWER_W('w'),
	LOWER_X('x'),
	LOWER_Y('y'),
	LOWER_Z('z'),

	LBRACE( '{' ),
	PIPE('|'),
	RBRACE('}'),
	TILDE('~'),

	SCANLINES(),
	BLOCK();

	public static char DEFAULT_CHAR;
	private static Map<Character,Glyph> lookup;

	public char underlying;
	public Vector3fl rawTexCoords;

	public static void setup() {
		lookup =  new HashMap<Character,Glyph>();
		for( Glyph g : Glyph.values() ) {

			int ix = g.ordinal();
			if( g.underlying != DEFAULT_CHAR)
				lookup.put( g.underlying , g );

			int stripCount = TextureRef.GUI.size / TextureRef.GUI.elementSize.x;
			g.rawTexCoords = new Vector3fl( ( ix % stripCount ), ( ix / stripCount ) ).multiply( TextureRef.GUI.elementSize );
			
		}
	}
	
	public static Glyph lookup( char c ) {
		return lookup.get( c );
	}
	
	private Glyph( char c ) {
		this.underlying = c;
	}
	
	private Glyph() {
	}
}
