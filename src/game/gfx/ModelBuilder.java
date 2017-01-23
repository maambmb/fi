package game.gfx;

import java.util.ArrayList;
import java.util.List;

import game.Config;
import util.Packer;
import util.Vector3f;

public class ModelBuilder {
	
	// given four vertices of a quad, this is the order in which their indices should
	// be specified in the index VBO
	private static final int[] QUAD_ENUMERATION = new int[] { 2, 1, 0, 3, 2, 0 };
	
	// the index VBO buffer
	private List<Integer> indices;
	// the extra (non-position) data buffer (contains all extra data in the form: eda0,edb0,edc0,eda1,edb1,edc1 etc.
	private List<Integer> extraData;
	// the position data buffer
	private List<Float> position;
	// a buffer used to ferry one type of extra data into vram
	private List<Integer> singleExtraData;
	// count of the number of vertices
	private int index;
	
	// buffer the position and extra data of a single vertex
	public Packer extraDataVertexBuffer;
	public Vector3f positionVertexBuffer;
    
    public ModelBuilder() {
    	// the extra data buffer must contain room for all non-pos bytes
    	this.extraDataVertexBuffer = new Packer( Config.VBO_NONPOS_BYTES );
    	this.positionVertexBuffer = new Vector3f();
    	this.extraData = new ArrayList<Integer>();
    	this.position = new ArrayList<Float>();
    	this.indices = new ArrayList<Integer>();
    	this.singleExtraData = new ArrayList<Integer>();
    	this.index = 0;
    }
    
    // commit a single vertex to the builder by adding the position and extra data to the buffers
    public void addVertex() {
    	this.position.add( this.positionVertexBuffer.x );
    	this.position.add( this.positionVertexBuffer.y );
    	this.position.add( this.positionVertexBuffer.z );
    	for( int i : this.extraDataVertexBuffer.packed )
    		this.extraData.add( i );
    	// wipe the extra data buffer in anticipation of the next vertex
    	this.extraDataVertexBuffer.reset();
    }

    // commit a quad to the builder (run after 4 vertices have been committed )
    // by adding their indices to the index buffer
    public void addQuad() {
    	for( int offset : QUAD_ENUMERATION )
    		this.indices.add( this.index + offset );
    	this.index += 4;
    }
    
    // wipe the state of the builder
    private void reset() {
    	this.extraDataVertexBuffer.reset();
    	this.extraData.clear();
    	this.position.clear();
    	this.indices.clear();
    	this.singleExtraData.clear();
    	this.index = 0;
    }
    
    // create a model from the builder by ferrying the data into VRAM using OpenGL
    public Model commit() {
    	
    	Model model = new Model( this.index );
    	model.addIndexData( this.indices );
    	model.addPositionData( this.position );
    	
    	for( int i = 0; i < Config.VBO_NONPOS_INTS; i += 1 ) {
			// for each type of extra data, skip along the entire extra data buffer, pulling out relevant values
    		// and putting them into a buffer before ferrying to VRAM
    		for( int n = 0; n < this.index; i += 1 )
    			this.singleExtraData.add( this.extraData.get( Config.VBO_NONPOS_INTS * n + i) );
    		model.addExtraData( i, this.singleExtraData );
    		// clear the buffer in anticipation of the next bit of extra data
			this.singleExtraData.clear();
    	}

    	// wipe the builder in anticipation of the next model
    	this.reset();
    	return model;
    }

}
