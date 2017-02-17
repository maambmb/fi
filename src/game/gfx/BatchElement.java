package game.gfx;

public interface BatchElement {

	int getDepth();
	
	void renderToBatch( Model batch );
	
}
