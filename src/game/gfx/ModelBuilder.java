package game.gfx;

import java.util.ArrayList;
import java.util.List;

import util.Vector3fl;

public class ModelBuilder {
	
	// given four vertices of a quad, this is the order in which their indices should
	// be specified in the index VBO
	private static final int[] QUAD_ENUMERATION = new int[] { 2, 1, 0, 3, 2, 0 };
	
	// the index VBO buffer
	private List<Integer> indices;
	// the extra (non-position) data buffer (contains all extra data in the form: eda0,edb0,edc0,eda1,edb1,edc1 etc.
	private List<Integer> attributeData;
	// the position data buffer
	private List<Float> positions;
	// a buffer used to ferry one type of extra data into vram
	private List<Integer> slicedAttributeData;
	// count of the number of vertices
	private int index;
	
	// buffer the position and extra data of a single vertex
	private int[] attributeDataBuffer;
	public Vector3fl positionBuffer;
    
    public ModelBuilder() {
    	// the extra data buffer must contain room for all non-pos bytes
    	this.attributeDataBuffer = new int[ AttributeVariable.values().length ];
    	this.positionBuffer = new Vector3fl();

    	this.attributeData = new ArrayList<Integer>();
    	this.positions = new ArrayList<Float>();

    	this.indices = new ArrayList<Integer>();
    	this.slicedAttributeData = new ArrayList<Integer>();
    	this.index = 0;
    }
    
    public void addAttributeData( AttributeVariable iv, int val ) {
    	this.attributeDataBuffer[ iv.ordinal() ] = val;
    }
    
    // commit a single vertex to the builder by adding the position and extra data to the buffers
    public void addVertex() {
    	this.positions.add( this.positionBuffer.x );
    	this.positions.add( this.positionBuffer.y );
    	this.positions.add( this.positionBuffer.z );
    	for( int i = 0; i < this.attributeDataBuffer.length; i += 1) {
    		this.attributeData.add( this.attributeDataBuffer[i] );
    		this.attributeDataBuffer[i] = 0;
    	}
    }

    // commit a quad to the builder (run after 4 vertices have been committed )
    // by adding their indices to the index buffer
    public void addQuad() {
    	for( int offset : QUAD_ENUMERATION )
    		this.indices.add( this.index + offset );
    	this.index += 4;
    }
    
    // create a model from the builder by ferrying the data into VRAM using OpenGL
    public Model commit() {
    	
    	Model model = new Model( this.index );
    	model.addIndexData( this.indices );
    	model.addPositionData( this.positions );
    	for( AttributeVariable iv : AttributeVariable.values() ) {
    		for( int i = 0; i < this.index; i += 1 )
    			this.slicedAttributeData.add( this.attributeData.get( iv.ordinal() + AttributeVariable.values().length * i) );
    		model.addAttributeData( iv, this.slicedAttributeData );
    		this.slicedAttributeData.clear();
    	}
    	
    	this.indices.clear();
    	this.positions.clear();
    	this.attributeData.clear();

    	return model;
    }

}
