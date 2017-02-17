package game.env;

import java.util.Random;

public enum Weather {

	CLEAR( 0.2f, 500f, 0.0f, 0.0f, 1f ),
	OVERCAST( 0.2f, 450f, 0.0f, 0.0f, 0.9f ),
	RAIN( 0.15f, 400f, 0.3f, 0.0f, 0.75f ),
	HEAVY_RAIN( 0.1f, 350f, 0.5f, 0.05f, 0.65f ),
	STORM( 0.05f, 0.45f, 230f, 0.2f, 0.45f ),
	SEVERE_STORM( 0.01f, 150f, 0.9f, 0.9f, 0.35f );
	
	public float probability;
	public float farDistance;
	public float rainAmount;
	public float thunderAmount;
	public float skyMultiplier;

	private Weather( float probability, float farDistance, float rainAmount, float thunderAmount, float skyMultiplier ) {
		this.probability = probability;
		this.farDistance = farDistance;
		this.rainAmount = rainAmount;
		this.thunderAmount = thunderAmount;
		this.skyMultiplier = skyMultiplier;
	}
	
	private static Random rng = new Random();
	public static void setup() {
		float total = 0;
		for( Weather w : Weather.values() )
			total += w.probability;
		for( Weather w : Weather.values() )
			w.probability /= total;
	}
	
	public static Weather getNextWeather() {
		float amt = rng.nextFloat();
		Weather next = Weather.CLEAR;
		for( Weather w : Weather.values() ) {
			amt -= w.probability;
			if( amt <= 0 ) {
				next = w;
				break;
			}
		}
		return next;
	}
	


}
