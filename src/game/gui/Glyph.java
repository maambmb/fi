package game.gui;

import java.util.HashMap;
import java.util.Map;

public enum Glyph {

	LOWER_A('a'),
	UPPER_A('A'),
	LOWER_B('b'),
	UPPER_B('B'),
	LOWER_C('c'),
	UPPER_C('C'),
	LOWER_D('d'),
	UPPER_D('D'),
	LOWER_E('e'),
	UPPER_E('E'),
	LOWER_F('f'),
	UPPER_F('F'),
	LOWER_G('g'),
	UPPER_G('G'),
	LOWER_H('h'),
	UPPER_H('H'),
	LOWER_I('i'),
	UPPER_I('I'),
	LOWER_J('j'),
	UPPER_J('J'),
	LOWER_K('k'),
	UPPER_K('K'),
	LOWER_L('l'),
	UPPER_L('L'),
	LOWER_M('m'),
	UPPER_M('M'),
	LOWER_N('n'),
	UPPER_N('N'),
	LOWER_O('o'),
	UPPER_O('O'),
	LOWER_P('p'),
	UPPER_P('P'),
	LOWER_Q('q'),
	UPPER_Q('Q'),
	LOWER_R('r'),
	UPPER_R('R'),
	LOWER_S('s'),
	UPPER_S('S'),
	LOWER_T('t'),
	UPPER_T('T'),
	LOWER_U('u'),
	UPPER_U('U'),
	LOWER_V('v'),
	UPPER_V('V'),
	LOWER_W('w'),
	UPPER_W('W'),
	LOWER_X('x'),
	UPPER_X('X'),
	LOWER_Y('y'),
	UPPER_Y('Y'),
	LOWER_Z('z'),
	UPPER_Z('Z'),
	
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
	
	LSQUARE( '['),
	RSQUARE( ']' ),
	LBRACE( '{' ),
	RBRACE('}'),
	SEMICOLON(';'),
	COLON(':'),
	COMMA(','),
	PERIOD('.'),
	LANGLE('<'),
	RANGLE('>'),
	DASH('-'),
	UNDERSCORE('_'),
	EQUALS('='),
	PLUS('+'),
	SPACE(' '),
	SLASH( '/' ),
	QUESTION('?'),
	GRAVE('`'),
	EXCLAMATION('!'),
	DQUOTE('"'),
	SQUOTE('\''),
	HASH('#'),
	DOLLAR('$'),
	PERCENT('%'),
	AMPERSAND('&'),
	LPARENS('('),
	RPARENS(')'),
	ASTERIX('*'),
	AT('@'),
	HAT('^'),
	BSLASH('\\'),
	PIPE('|'),
	TILDE('~'),

	SCANLINES(),
	BLOCK(),
	
	// invisible glyphs
	NEWLINE();
	
	public static char DEFAULT_CHAR;
	private static Map<Character,Glyph> lookup;
	private char lookupChar;

	public static void init() {
		lookup =  new HashMap<Character,Glyph>();
		for( Glyph g : Glyph.values() )
			if( g.lookupChar != DEFAULT_CHAR)
				lookup.put( g.lookupChar , g );
	}
	
	public static Glyph lookup( char c ) {
		return lookup.get( c );
	}
	
	private Glyph( char c ) {
		this.lookupChar = c;
	}
	
	private Glyph() {
	}
}
