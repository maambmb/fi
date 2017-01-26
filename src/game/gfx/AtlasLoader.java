package game.gfx;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import game.Entity;

public class AtlasLoader extends Entity {

    // a class which lazilly loads in texture atlases
    // and also handles cleanup when the entity is destroyed

    // a texture handle - holds the texture id and its size
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

    // the internal cache which maps texture path to handle
    private Map<String,TextureRef> texMap;

    public AtlasLoader() {
        this.texMap = new HashMap<String,TextureRef>();
    }

    // return the texture handle if it already exists
    // otherwise load in the texture using Slick2D, create and persist the handle
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
    // when this entity is destroyed, make sure we delete all the textures
    public void destroy() {
        super.destroy();
        for( TextureRef tex : this.texMap.values() )
            GL11.glDeleteTextures( tex.id );
    }

}
