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
        for( int i = 0; i < 800; i +=1 )
            this.update();
        World.WORLD.refresh();
    }

    private void update() {

        Vector3in nextPos = new Vector3in(
            this.rng.nextInt( 20 ) - 10,
            this.rng.nextInt( 20 ) - 10,
            this.rng.nextInt( 20 ) - 10
        );

        World.WORLD.setBlock( nextPos, BlockType.GRAVEL );
    }

    @Override
    protected void registerComponents() {
    }

}
