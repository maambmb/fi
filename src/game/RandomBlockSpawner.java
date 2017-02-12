package game;

import java.util.ArrayList;
import java.util.List;
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
        List<Vector3in> posTracker = new ArrayList<Vector3in>();
        for( int i = 0; i < 3000; i +=1 ) {
        	int x = this.rng.nextInt( 34 ) - 17;
        	int y = this.rng.nextInt( 34 ) - 17;
        	int z = this.rng.nextInt( 34 ) - 17;

        	BlockType bt = BlockType.AIR;
        	while( bt.opacity != BlockType.Opacity.OPAQUE ) {
				int nextIx = this.rng.nextInt( BlockType.values().length );
				bt = BlockType.values()[ nextIx ];
        	}

        	Vector3in v = new Vector3in(x,y,z);
        	posTracker.add(v);
        	World.WORLD.setBlock( v, bt );
        }
        
        for( Vector3in v : posTracker ) {
        	Vector3in above = v.add( Vector3in.CubeNormal.TOP.vector );

        	BlockType bt = BlockType.AIR;
        	while( bt.opacity != BlockType.Opacity.CROSSED ) {
				int nextIx = this.rng.nextInt( BlockType.values().length );
				bt = BlockType.values()[ nextIx ];
        	}
        	
        	if( bt.illumination.toMaxElement() > 0 && rng.nextInt(30) != 0 )
        		bt = BlockType.LUSH_SHRUBS_1;

        	if( World.WORLD.getBlock( above ).blockType == BlockType.AIR )
        		World.WORLD.setBlock( above, bt);
        }
        World.WORLD.refresh();
    }

    @Override
    protected void registerComponents() {
    }

}
