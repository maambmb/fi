package game;

import game.block.LightSource;
import game.gfx.UniformVariable;
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
    public Vector3fl globalLightOrigin;
    public Vector3fl fogColor;

    private Environment() {

        this.lighting     = new Vector3fl[ LightSource.values().length ];
        this.baseLighting = new Vector3fl();
        this.fogColor     = new Vector3fl();

        this.globalLightOrigin = new Vector3fl();

        for( int i = 0; i < this.lighting.length; i += 1 )
            this.lighting[i] = new Vector3fl();
        this.globalListenerClient.addListener( BlockShader.BlockShaderPrepareMessage.class, (msg) -> {
            this.shaderPrepare( BlockShader.SHADER );
        });
    }

    private void shaderPrepare( Shader s ) {
        for( LightSource src : LightSource.values() )
            s.loadVector3fl( src.uniformVariable, this.lighting[ src.ordinal() ] );

        s.loadVector3fl( UniformVariable.LIGHTING_BASE, this.baseLighting );
        s.loadVector3fl( UniformVariable.FOG_COLOR, this.fogColor );
        s.loadVector3fl( UniformVariable.GLOBAL_LIGHT_ORIGIN, this.globalLightOrigin );
    }

    @Override
    protected void addComponents() {

    }

}
