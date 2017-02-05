package game.gfx.font;

import game.gfx.TextureRef;
import util.Vector3in;

public interface FontMap {

	TextureRef getTexture();
	Vector3in getDims();
	Vector3in getPosition( char c );
		
	public static Vector3in calculatePosition( int sheetWidth, Vector3in charDim, int ix ) {
		int numChars = sheetWidth / charDim.x;
		int xPos = ( ix % numChars ) * charDim.x;
		int yPos = ( ix / numChars ) * charDim.y;
		return new Vector3in( xPos, yPos, 0 );
	}
}
