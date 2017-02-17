package game.env;

import java.util.Random;

import game.Entity;
import game.Game.UpdateMessage;
import game.GlobalSubscriberComponent;
import game.block.BlockShader;
import game.block.LightSource;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import game.particle.Rain;
import util.Vector3fl;
import util.Vector3in;

public final class Environment extends Entity {

    public static Environment GLOBAL;
    public static void setup() {
        GLOBAL = new Environment();
    }

    public Vector3in[] lighting;
    public Vector3in baseLighting;
    public Vector3in fogColor;

    private Random rng;
    
    private DayState previousDayState;
    private DayState dayState;

    private Weather previousWeather;
    private Weather weather;
    
    private float rainAmount;
    public float maxDistance;

    private long currStateDuration;

    public Vector3fl lightOrigin;
    
    private Environment() {
    	super();
    	
        this.lighting     = new Vector3in[ LightSource.values().length ];
        this.baseLighting = new Vector3in( 0x202020 );
        this.fogColor     = new Vector3in( 0x101010 );
        this.lightOrigin  = new Vector3fl(1,1,1);
        
        this.rng = new Random();

        this.dayState = DayState.MIDDAY;
        this.previousDayState = DayState.MIDDAY_START;
        this.weather = Weather.HEAVY_RAIN;
        this.previousWeather = Weather.CLEAR;
        
        for( int i = 0; i < this.lighting.length; i += 1 )
            this.lighting[i] = new Vector3in(0xFFFFFF);
        
        this.listener.addSubscriber( UpdateMessage.class, this::update );
    	this.registerComponent( new GlobalSubscriberComponent( this ) );
        this.listener.addSubscriber( BlockShader.BlockShaderPreRenderMessage.class, this::blockShaderPreRender );
        this.build();
    }

    private void update( UpdateMessage msg ) {

    	this.currStateDuration += msg.deltaMs;

    	if( this.currStateDuration > this.dayState.durationMs) {
    		this.currStateDuration -= this.dayState.durationMs;
    		this.previousDayState = this.dayState;
			this.previousWeather = this.weather;
    		this.dayState = this.dayState.nextDayState();
    		if( this.dayState == DayState.MIDDAY )
    			this.weather = Weather.getNextWeather();
    		System.out.println( this.dayState );
    		System.out.println( this.weather );
    	}

    	float progress = (float)this.currStateDuration / this.dayState.durationMs;

    	Vector3fl previousLight = this.previousDayState.skyColor.toVector3fl().multiply( 1f - progress )
				.multiply( this.previousWeather.skyMultiplier );

    	Vector3fl currentLight = this.dayState.skyColor.toVector3fl().multiply( progress )
    			.multiply( this.weather.skyMultiplier );
    	
    	Vector3in lighting = previousLight.add( currentLight ).toRoundedVector3in();
    	int nightLightAmount = 255 - lighting.toMaxElement();
    	
    	this.rainAmount = ( 1 - progress) * this.previousWeather.rainAmount + progress * this.weather.rainAmount;
    	this.maxDistance = ( 1 - progress ) * this.previousWeather.farDistance + progress * this.weather.farDistance;
    	
		if( this.rng.nextFloat() <= this.rainAmount ) {
			Rain r = new Rain();
			r.position.position.x = this.rng.nextInt(20);
			r.position.position.z = this.rng.nextInt(20);
			r.position.position.y = 20;
		}
    	
    	this.lighting[ LightSource.GLOBAL.ordinal() ] = this.fogColor = lighting;
    	this.lighting[ LightSource.NIGHT.ordinal() ] = new Vector3in( nightLightAmount, nightLightAmount, nightLightAmount );   
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
