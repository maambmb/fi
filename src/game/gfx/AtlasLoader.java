package game.gfx;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import game.Entity;

public class AtlasLoader extends Entity {

    public class TextureRef {
        public int id;
        public int size;
        public TextureRef( int id, int size ) {
            this.id = id;
            this.size = size;
        }
    }

    public static AtlasLoader LOADER;
    public static void init() {
        LOADER = new AtlasLoader();
    }

    private Map<String,TextureRef> texMap;
    public AtlasLoader() {
        this.texMap = new HashMap<String,TextureRef>();
    }

    public TextureRef getTexture( String path ) {
        if( !this.texMap.containsKey( path ) ) {
            try {
                Texture tex = TextureLoader.getTexture( "PNG", new FileInputStream( path ) );
                this.texMap.put( path, new TextureRef( tex.getTextureID(), tex.getTextureWidth() ) );
            }
            catch( Exception e ) { 
                throw new RuntimeException( "Unable to load texture" ); }
        }
        return this.texMap.get( path );
    }

    @Override
    public void addComponents() {
    }

    @Override
    public void destroy() {
        super.destroy();
        for( TextureRef tex : this.texMap.values() )
            GL11.glDeleteTextures( tex.id );
    }

}
