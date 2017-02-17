package game;

import java.util.Random;

import game.Game.UpdateMessage;
import game.block.Block;
import game.block.World;
import game.particle.Rain;
import util.Vector3in;

public final class RandomBlockSpawner extends Entity {

    private Random rng;

    public RandomBlockSpawner() {
        super();
        this.rng = new Random();
        this.spawnSome();
        this.registerComponent( new GlobalSubscriberComponent( this ) );
        this.build();
    }
    
    public void spawnSome() {
        for( int i = 0; i < 500; i += 1 ) {
        	int x = this.rng.nextInt(20);
        	int z = this.rng.nextInt(20);
        	int y = this.rng.nextInt(20);

        	Block b = Block.DOTTED_CYAN;
			World.WORLD.setBlock( new Vector3in(x,y,z), b );
        }
    }

}
