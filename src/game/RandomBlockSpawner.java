package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.block.BlockContext;
import game.block.Block;
import game.block.World;
//import game.listener.Listener;
import util.Vector3in;

public final class RandomBlockSpawner extends Entity {

    private Random rng;

    public RandomBlockSpawner() {
        super();
        this.rng = new Random();
        List<Vector3in> posTracker = new ArrayList<Vector3in>();
        for( int i = 0; i < 6000; i +=1 ) {
        	int x = this.rng.nextInt( 16 );
        	int y = this.rng.nextInt( 200 );
        	int z = this.rng.nextInt( 16 );

        	Block bt = Block.AIR;
        	while( bt.opacity != Block.Opacity.OPAQUE ) {
				int nextIx = this.rng.nextInt( Block.values().length );
				bt = Block.values()[ nextIx ];
        	}

        	Vector3in v = new Vector3in(x,y,z);
        	posTracker.add(v);
        }
        

        World.WORLD.refresh();
    }

    @Override
    protected void registerComponents() {
    }

}
