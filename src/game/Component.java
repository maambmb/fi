package game;

public interface Component {

    void init();

    // clean up any resources this component may hold (I'm looking at you VRAM)
    void destroy();

}
