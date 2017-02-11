package game;

import game.block.BlockShader;
import game.block.LightSource;
import game.gfx.GlobalSubscriberComponent;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import util.Vector3fl;
import util.Vector3in;

public final class Environment extends Entity {

    public static Environment GLOBAL;
    public static void init() {
        GLOBAL = new Environment();
    }

    public Vector3in[] lighting;
    public Vector3in baseLighting;
    public Vector3in fogColor;
    public float maxDistance = 50f;

    public Vector3fl lightOrigin;

    private Environment() {
    	super();
    	
        this.lighting     = new Vector3in[ LightSource.values().length ];
        this.baseLighting = new Vector3in( 0xFFFFFF );
        this.fogColor     = new Vector3in( 0x00FFFF );

        this.lightOrigin = new Vector3fl(1,1,1);

        for( int i = 0; i < this.lighting.length; i += 1 )
            this.lighting[i] = new Vector3in(0xFFFFFF);
    }

    @Override
    protected void registerComponents() {
    	this.registerComponent( new GlobalSubscriberComponent() );
        this.listener.addSubscriber( BlockShader.BlockShaderPreRenderMessage.class, this::blockShaderPreRender );
    }

    private void preRender( Shader s ) {
        for( LightSource src : LightSource.values() )
            s.loadInt( src.uniformVariable, this.lighting[ src.ordinal() ].toPackedBytes() );

        s.loadInt( UniformVariable.LIGHTING_BASE, this.baseLighting.toPackedBytes() );
        s.loadInt( UniformVariable.FOG_COLOR, this.fogColor.toPackedBytes() );
        s.loadVector3fl( UniformVariable.LIGHT_ORIGIN, this.lightOrigin );
        s.loadFloat( UniformVariable.MAX_DISTANCE, this.maxDistance );
    }
    
    private void blockShaderPreRender( BlockShader.BlockShaderPreRenderMessage msg ) {
    	this.preRender( BlockShader.GLOBAL );
    }

}
