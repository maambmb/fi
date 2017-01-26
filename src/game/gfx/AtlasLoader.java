package game.gfx;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class AtlasLoader {

    public static AtlasLoader LOADER;
    public static void init() {
        LOADER = new AtlasLoader();
    }

    private Map<String,Integer> idMap;
    public AtlasLoader() {
        this.idMap = new HashMap<String,Integer>();
    }

    public int getTexture( String path ) {
        if( !this.idMap.containsKey( path ) ) {
            try {
                Texture tex = TextureLoader.getTexture( "PNG", new FileInputStream( path ) );
                this.idMap.put( path, tex.getTextureID() );
            }
            catch( Exception e ) { 
                throw new RuntimeException( "Unable to load texture" );
            }
        }
        return this.idMap.get( path );
    }

    public void destroy() {
        for( int id : this.idMap.values() )
            GL11.glDeleteTextures( id );
    }

}
