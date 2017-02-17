package game.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import util.Vector3fl;
import util.Vector3in;

public class Model {

	private static int[] QUAD_ENUMERATION 	= new int[] { 0, 1, 2, 2, 3, 0 };

	public static Vector3fl[] QUAD_VERTICES = new Vector3fl[] { 
		new Vector3fl(-1,-1,1), 
		new Vector3fl(1,-1,1), 
		new Vector3fl(1,1,1), 
		new Vector3fl(-1,1,1) 
	};

    private static IntBuffer intBufferFromCollection( Collection<Object> coll ) {
        IntBuffer buf = BufferUtils.createIntBuffer( coll.size() );
        for( Object f : coll )
            buf.put( (int)f );
        buf.flip();
        return buf;
    }

    private static FloatBuffer floatBufferFromCollection( Collection<Object> coll ) {
        FloatBuffer buf = BufferUtils.createFloatBuffer( coll.size() );
        for( Object f : coll )
            buf.put( (float)f );
        buf.flip();
        return buf;
    }

    public int vertexCount;
    private int indexCount;
    private boolean built;
    private int indexVbo;
    private BufferType bufferType;

    private List<Object> indices;

    private Map<AttributeVariable,List<Object>> buffers;
    private Map<AttributeVariable,Integer> vboMap;
    private int vaoId;

    private TextureRef texture;

    public Model( TextureRef tex, Collection<AttributeVariable> usedAvs, BufferType bufferType ) {
    	
        this.indices     = new ArrayList<Object>();
        this.buffers     = new HashMap<AttributeVariable,List<Object>>();
        this.vboMap      = new HashMap<AttributeVariable,Integer>();
        this.bufferType  = bufferType;
        this.texture     = tex;

        this.vaoId = GL30.glGenVertexArrays();
		this.indexVbo = GL15.glGenBuffers();
        GL30.glBindVertexArray( this.vaoId );

        for( AttributeVariable av : usedAvs ) {

        	int vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

            if( av.dataType == Integer.class )
                GL30.glVertexAttribIPointer( av.ordinal(), av.stride, GL11.GL_INT, 0, 0  );
            else if ( av.dataType == Float.class )
                GL20.glVertexAttribPointer( av.ordinal(), av.stride, GL11.GL_FLOAT, false, 0, 0);
            
			GL20.glEnableVertexAttribArray( av.ordinal() );

			this.vboMap.put( av, vboId );
        	this.buffers.put( av, new ArrayList<Object>() );
        }
    }

    // adding attribute data happens below. Type safety is pretty
    // ropey at this point so be careful. Quite easy to mix up float and
    // ints if not paying attention...

    // add a single int to an av buffer
    public void addAttributeData( AttributeVariable av, int val ) {
        this.buffers.get( av ).add( val );
    }

    // add a single float to an av buffer
    public void addAttributeData( AttributeVariable av, float val ) {
        this.buffers.get( av ).add( val );
    }

    // add a vec of floats to an av buffer (stride 3)
    public void addAttributeData( AttributeVariable av, Vector3fl val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
        buffer.add( val.z );
    }

    // add a vec of ints to an av buffer (stride 3)
    public void addAttributeData( AttributeVariable av, Vector3in val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
        buffer.add( val.z );
    }

    // add a vec of floats to an av buffer (stride 2)
    // pretend its a 2D vector (i.e. ignore the .z component )
    public void addAttributeData2D( AttributeVariable av, Vector3fl val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
    }

    // add a vec of ints to an av buffer (stride 2)
    // pretend its a 2D vector (i.e. ignore the .z component )
    public void addAttributeData2D( AttributeVariable av, Vector3in val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
    }

    // record a new quad by adding 4 to the vertex count
    // and adding the 6 new indices to the index vbo buffer
    public void addQuad() {
    	for( int i =0; i < QUAD_ENUMERATION.length; i += 1)
            this.indices.add( this.indexCount + QUAD_ENUMERATION[i] );
        this.indexCount += 4;
    }
    
    public void addFlippedQuad() {
    	for( int i = QUAD_ENUMERATION.length-1; i >= 0; i -= 1 )
            this.indices.add( this.indexCount + QUAD_ENUMERATION[i] );
        this.indexCount += 4;
    }

    public void buildModel() {

    	this.built = true;
        GL30.glBindVertexArray( this.vaoId );
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indexVbo );
        IntBuffer ixBuf = intBufferFromCollection( this.indices );
        GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, ixBuf, this.bufferType.bufferType );
        
        this.indexCount = 0;
        this.vertexCount = this.indices.size();
        this.indices.clear();

		for( AttributeVariable av : this.buffers.keySet() ) {

            Collection<Object> buffer = this.buffers.get( av );
            int vboId = this.vboMap.get( av );
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

            if( av.dataType == Integer.class ) {
                IntBuffer buf = intBufferFromCollection( buffer );
                GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, this.bufferType.bufferType );
            // else do the same but with a float buffer
            } else if ( av.dataType == Float.class ) {
                FloatBuffer buf = floatBufferFromCollection( buffer );
                GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, this.bufferType.bufferType );
            }

            buffer.clear();
        }
    }

    // jump through and delete all VBOs
    // before deleting the VAO itself
    public void destroy() {
    	
		GL15.glDeleteBuffers(this.indexVbo);
        for( int vboId : this.vboMap.values() )
            GL15.glDeleteBuffers(vboId);
        GL30.glDeleteVertexArrays(this.vaoId);

    }

    public void render() {
    	if( !this.built )
    		return;
        // bind and activate the model's texture atlas
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, this.texture.id );
        GL13.glActiveTexture( GL13.GL_TEXTURE0 );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST );
        GL11.glTexParameteri( GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST );

        GL30.glBindVertexArray( this.vaoId );

        GL11.glDrawElements( GL11.GL_TRIANGLES, this.vertexCount, GL11.GL_UNSIGNED_INT, 0 );
    }

}
