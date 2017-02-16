package game;

import java.util.Random;

import game.block.Block;
import game.block.World;
import game.block.Block.Opacity;
import util.Vector3in;

public final class RandomBlockSpawner extends Entity {

    private Random rng;

    public RandomBlockSpawner() {
        super();
        this.rng = new Random();
        
        for( int i = 0; i < 64000; i += 1 ) {
        	int x = this.rng.nextInt(64);
        	int z = this.rng.nextInt(64);
        	int y = this.rng.nextInt(64);

        	Block b = Block.LUSH_SHRUBS_1;
			World.WORLD.setBlock( new Vector3in(x,y,z), b );
        }
    }

    @Override
    protected void registerComponents() {
    }

}
