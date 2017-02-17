package game.gfx;

import java.util.ArrayList;
import java.util.List;

public class Batcher {

	private static int batchComparator( BatchElement l, BatchElement r ) {
		return l.getDepth() - r.getDepth();
	}

	private List<BatchElement> batchElements; 

	public Batcher() {
		this.batchElements = new ArrayList<BatchElement>();
	}
	
	public void addToBatch( BatchElement el ) {
		this.batchElements.add( el );
	}
	
	public void render( Model model ) {
		this.batchElements.sort( Batcher::batchComparator );
		for( BatchElement el : this.batchElements )
			el.renderToBatch( model );
		this.batchElements.clear();
		model.buildModel();
		model.render();
	}
}
