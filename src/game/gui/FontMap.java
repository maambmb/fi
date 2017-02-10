package game.gui;

import java.util.HashMap;
import java.util.Map;

import game.gfx.TextureRef;
import util.Vector3fl;
import util.Vector3in;

public enum FontMap {
	
	DEBUG( TextureRef.FONT_DEBUG, new Vector3in(7,9,0), 
		" !\"#$%&'()*+,-./01" +
		"23456789:;<=>?@ABC" +
		"DEFGHIJKLMNOPQRSTU" +
		"VWXYZ[\\]^_`abcdefg" +
		"hijklmnopqrstuvwxy" +
		"z{|}~"
	);

	public TextureRef atlas;
	public Vector3fl charDimensions;

	private int numChars;
	private Vector3fl missingPos;
	private Map<Character,Vector3fl> lookup;
	
	private FontMap( TextureRef atlas, Vector3in charDimensions, String sheetSpec ) {
		this.atlas = atlas;
		this.numChars = atlas.size / charDimensions.x;
		this.charDimensions = charDimensions.toVector3fl().divide( atlas.size );
		
		this.lookup = new HashMap<Character,Vector3fl>();
		char[] chars = sheetSpec.toCharArray();
		for( int i = 0; i < chars.length; i += 1)
			this.lookup.put( chars[i], this.calculatePosition( i ) );
		this.missingPos = this.calculatePosition( chars.length );
	}
	
	public Vector3fl getPosition( char c ) {
		return this.lookup.getOrDefault( c, this.missingPos );
	}
		
	private Vector3fl calculatePosition( int ix ) {
		float xPos = ( ix % this.numChars ) * this.charDimensions.x;
		float yPos = ( ix / this.numChars ) * this.charDimensions.y;
		return new Vector3fl( xPos, yPos, 0 );
	}
}
