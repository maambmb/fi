package game;

import java.util.Random;

import game.block.BlockType;
import game.block.World;
//import game.listener.Listener;

import util.Vector3in;

public final class RandomBlockSpawner extends Entity {

    private Random rng;

    public RandomBlockSpawner() {
        super();
        this.rng = new Random();
        for( int i = 0; i < 500; i +=1 ) {
        	int x = this.rng.nextInt( 14 ) - 7;
        	int y = this.rng.nextInt( 400 ) - 100;
        	int z = this.rng.nextInt( 14 ) - 7;
        	BlockType bt = i % 80 == 0 ? BlockType.GLOW_BLOCK : BlockType.GRAVEL;
        	World.WORLD.setBlock( new Vector3in( x,y,z ), bt );
        }
        World.WORLD.refresh();
    }

    @Override
    protected void registerComponents() {
    }

}
