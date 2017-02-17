package game.gfx;

import java.io.FileInputStream;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import game.Game.DestroyMessage;
import game.Listener;
import util.Vector3fl;
import util.Vector3in;

public enum TextureRef {

	BLOCK( "tex/block.png", new Vector3in(32,32), 512 ),
	PARTICLE( "tex/particle.png", new Vector3in(16,16), 128 ),
	GUI( "tex/gui.png", new Vector3in(7,9), 128 );
	
	public int id;
	public int size;
	public Vector3in elementSize;
	private String path;
	
	public static void setup() {
		for( TextureRef tr : TextureRef.values() ) {
			try {
				Texture tex = TextureLoader.getTexture( "PNG", new FileInputStream( tr.path ) );
				if( tex.getTextureWidth() != tr.size || tex.getTextureHeight() != tr.size )
					throw new RuntimeException( String.format( "Expected texture size mismatch: %s", tr.path ) );
				tr.id = tex.getTextureID();
			} catch( IOException e ) {
				throw new RuntimeException( "Unable to load texture");
			}
		}
	}
	
	public Vector3fl getGlCoords( Vector3in v ) {
		return v.toVector3fl().divide( this.size );
	}
	
	private TextureRef( String path, Vector3in elementSize, int dimension ) {
		this.path = path;
		this.size = dimension;
		this.elementSize = elementSize;
		Listener.GLOBAL.addSubscriber( DestroyMessage.class, this::destroy );
	}
	
	private void destroy( DestroyMessage m ) {
		GL11.glDeleteTextures( this.id );
	}
	
}
