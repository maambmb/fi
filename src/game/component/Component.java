package game.component;

import game.Entity;

public interface Component {

    // subscribe to global or entity-local messages in this method
    // also subscribe to requisite components as they are broadcast
    // as messages after all components have been setup
    void setup( Entity e );

    // clean up any resources this component may hold (I'm looking at you VRAM)
    void destroy();

}
