package game;

import game.block.LightSource;
import game.gfx.shader.BlockShader;
import game.gfx.shader.Shader;

import util.Vector3fl;

public class Environment extends Entity {

    public static Environment ENV;
    public static void init() {
        ENV = new Environment();
    }

    public Vector3fl[] lighting;
    public Vector3fl baseLighting;
    public Vector3fl lightSource;
    public Vector3fl fogColor;

    private Environment() {
        this.lighting          = new Vector3fl[ LightSource.values().length ];
        this.baseLighting      = new Vector3fl();
        this.fogColor          = new Vector3fl();
        this.lightSource = new Vector3fl();
        for( int i = 0; i < this.lighting.length; i += 1 )
            this.lighting[i] = new Vector3fl();
        this.globalListenerClient.addListener( BlockShader.BlockShaderPrepareMessage.class, (msg) -> {
            this.shaderPrepare( BlockShader.SHADER );
        });
    }

    private void shaderPrepare( Shader s ) {
        for( LightSource src : LightSource.values() )
            s.loadLighting( src, this.lighting[ src.ordinal() ] );
        s.loadBaseLighting( this.baseLighting );
        s.loadFogColor( this.fogColor );
        s.loadLightSource( this.lightSource );
    }

    @Override
    protected void addComponents() {
        // no components to add yet...
    }

}
