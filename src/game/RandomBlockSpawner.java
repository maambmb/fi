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
        this.spawnSome();
        
    }
    
    public void spawnSome() {
        for( int i = 0; i < 500; i += 1 ) {
        	int x = this.rng.nextInt(20);
        	int z = this.rng.nextInt(20);
        	int y = this.rng.nextInt(20);

        	Block b = Block.DASHED_SAND;
			World.WORLD.setBlock( new Vector3in(x,y,z), b );
        }
    }

    @Override
    protected void registerComponents() {
    }

}
