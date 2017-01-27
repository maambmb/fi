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
        //Listener.GLOBAL_LISTENER.addSubcriber( Game.UpdateMessage.class, this::update );
        this.rng = new Random();
        this.setup();
        for( int i = 0; i < 100; i +=1 )
            this.update();
        World.WORLD.refresh();
    }

    private void update() {

        Vector3in nextPos = new Vector3in(
            this.rng.nextInt( 500 ) - 250,
            this.rng.nextInt( 500 ) - 250,
            this.rng.nextInt( 500 ) - 250
        );

        World.WORLD.setBlock( nextPos, BlockType.GRAVEL, false );
    }

    @Override
    protected void addComponents() {
    }

}
