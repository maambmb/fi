package game;

import game.block.BlockShader;
import game.block.LightSource;
import game.gfx.GlobalSubscriberComponent;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import util.Vector3fl;

public final class Environment extends Entity {

    public static Environment GLOBAL;
    public static void init() {
        GLOBAL = new Environment();
    }

    public Vector3fl[] lighting;
    public Vector3fl baseLighting;
    public Vector3fl globalLightOrigin;
    public Vector3fl fogColor;

    private Environment() {
    	super();
    	
        this.lighting     = new Vector3fl[ LightSource.values().length ];
        this.baseLighting = new Vector3fl(0.5f,0.5f,0.5f);
        this.fogColor     = new Vector3fl(0.5f,0.5f,0.5f);

        this.globalLightOrigin = new Vector3fl();

        for( int i = 0; i < this.lighting.length; i += 1 )
            this.lighting[i] = new Vector3fl(1,1,1);
    }

    @Override
    protected void registerComponents() {
    	this.registerComponent( new GlobalSubscriberComponent() );
        this.listener.addSubscriber( BlockShader.BlockShaderPreRenderMessage.class, this::blockShaderPreRender );
    }

    private void preRender( Shader s ) {
        for( LightSource src : LightSource.values() )
            s.loadVector3fl( src.uniformVariable, this.lighting[ src.ordinal() ] );

        s.loadVector3fl( UniformVariable.LIGHTING_BASE, this.baseLighting );
        s.loadVector3fl( UniformVariable.FOG_COLOR, this.fogColor );
        s.loadVector3fl( UniformVariable.GLOBAL_LIGHT_ORIGIN, this.globalLightOrigin );
    }
    
    private void blockShaderPreRender( BlockShader.BlockShaderPreRenderMessage msg ) {
    	this.preRender( BlockShader.GLOBAL );
    }

}
