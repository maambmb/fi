package game.gfx.font;

import java.util.HashMap;
import java.util.Map;

import game.Config;
import game.gfx.TextureRef;
import util.Vector3in;

public class DebugFontMap implements FontMap {

	private static Vector3in dims = new Vector3in( 128, 64, 0 );
	private static Vector3in charDim = new Vector3in( 7,9, 0);
	private static Vector3in missingPos = new Vector3in( 119, 54, 0 );
	
	private static String sheetChars = 
		" !\"#$%&'()*+,-./01" +
		"23456789:;<=>?@ABC" +
		"DEFGHIJKLMNOPQRSTU" +
		"VWXYZ[\\]^_`abcdefg" +
		"hijklmnopqrstuvwxy" +
		"z{|}~";
	
	public DebugFontMap GLOBAL;
	public void init() {
		GLOBAL = new DebugFontMap();
	}

	private Map<Character,Vector3in> lookup;
	
	public DebugFontMap() {
		this.lookup = new HashMap<Character,Vector3in>();
		char[] chars = sheetChars.toCharArray();
		for( int i = 0; i < chars.length; i += 1)
			this.lookup.put( chars[i], FontMap.calculatePosition( dims.x, charDim, i ) );
	}
	
	@Override
	public TextureRef getTexture() {
		return TextureRef.FONT_DEBUG;
	}

	@Override
	public Vector3in getDims() {
		return dims;
	}

	@Override
	public Vector3in getPosition(char c) {
		return this.lookup.getOrDefault( c, missingPos );
	}

}
