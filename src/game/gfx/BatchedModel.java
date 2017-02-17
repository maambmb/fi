package game.gfx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BatchedModel extends Model {

	private static int batchComparator( BatchElement l, BatchElement r ) {
		return l.getDepth() - r.getDepth();
	}

	private List<BatchElement> batchElements; 

	public BatchedModel(TextureRef tex, Collection<AttributeVariable> usedAvs, BufferType bufferType) {
		super(tex, usedAvs, bufferType);
		this.batchElements = new ArrayList<BatchElement>();
	}
	
	public void addToBatch( BatchElement el ) {
		this.batchElements.add( el );
	}
	
	@Override
	public void render() {
		this.batchElements.sort( BatchedModel::batchComparator );
		for( BatchElement el : this.batchElements )
			el.renderToBatch( this );
		this.batchElements.clear();
		this.buildModel();
		super.render();
	}

}
