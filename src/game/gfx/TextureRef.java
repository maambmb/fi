package game.gfx;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import util.Vector3fl;
import util.Vector3in;

public enum TextureRef {

	BLOCK( "tex/block.png", 512 ),
	FONT_DEBUG( "fnt/debug.png", 128 );
	
	public int id;
	public int size;
	private String path;
	
	public static void init() {
		for( TextureRef tr : TextureRef.values() ) {
			try {
				Texture tex = TextureLoader.getTexture( "PNG", new FileInputStream( tr.path ) );
				if( tex.getTextureWidth() != tr.size || tex.getTextureHeight() != tr.size )
					throw new RuntimeException( "Expected texture size mismatch");
				tr.id = tex.getTextureID();
			} catch( IOException e ) {
				throw new RuntimeException( "Unable to load texture");
			}
		}

	}
	
	public static void destroy() {
		for( TextureRef ref : TextureRef.values() )
			GL11.glDeleteTextures( ref.id );
	}
	
	public Vector3fl getGlCoords( Vector3in v ) {
		return v.toVector3fl().divide( this.size );
	}
	
	private TextureRef( String path, int size ) {
		this.path = path;
		this.size = size;
	}
	
}
