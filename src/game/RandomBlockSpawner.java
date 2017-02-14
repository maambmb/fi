package game;

import java.util.Random;

import game.block.Block;
import game.block.World;
import util.Vector3in;

public final class RandomBlockSpawner extends Entity {

    private Random rng;

    public RandomBlockSpawner() {
        super();
        this.rng = new Random();
        
        for( int i = 0; i < 4000; i += 1 ) {
        	int x = this.rng.nextInt(20);
        	int z = this.rng.nextInt(20);
        	int y = this.rng.nextInt(20);

        	Block b = Block.WAVY_PURPLE;
        	int next = this.rng.nextInt(100);
        	if( next == 0 )
        		b = Block.GREEN_GLOWSHROOM;
        	if( next == 1 )
        		b = Block.PURPLE_GLOWSHROOM;
			World.WORLD.setBlock( new Vector3in(x,y,z), b );
        }
    }

    @Override
    protected void registerComponents() {
    }

}
