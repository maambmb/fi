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

import game.gfx.shader.BlockShader;
import util.Vector3fl;
import util.Vector3in;

public class Model {

    // convert a collection of boxed integers (cast down to objects - for reasons explained below )
    // into an int buffer
    private static IntBuffer intBufferFromCollection( Collection<Object> coll ) {
        IntBuffer buf = BufferUtils.createIntBuffer( coll.size() );
        for( Object f : coll )
            buf.put( (int)f );
        buf.flip();
        return buf;
    }

    // convert a collection of boxed floats (also cast down to objects) into a float buffer
    private static FloatBuffer floatBufferFromCollection( Collection<Object> coll ) {
        FloatBuffer buf = BufferUtils.createFloatBuffer( coll.size() );
        for( Object f : coll )
            buf.put( (float)f );
        buf.flip();
        return buf;
    }

    // given four vertices of a quad (i:0,1,2,3), this is the order in which they should
    // be drawn - for use in the index VBO
    private static final int[] ENUMERATION = new int[] { 3, 1, 0, 3, 0, 2};

    public int vertexCount;
    private int indexCount;

    // the index VBO buffer
    private List<Object> indices;

    // attribute variable buffers. We cast down to list of objects
    // because we want to store lists of both ints and floats in the same buffer map
    // for convenience
    private Map<AttributeVariable,List<Object>> buffers;

    // opengl id tracking
    private List<Integer> vboIds;
    private int vaoId;

    // the texture atlas' opengl id
    public int atlasId;

    public Model() {
        this.indices     = new ArrayList<Object>();
        this.buffers     = new HashMap<AttributeVariable,List<Object>>();
        this.vboIds      = new ArrayList<Integer>();
        this.vertexCount = 0;
        this.indexCount  = 0;
    }

    // prepare the model to use the specified attribute variable
    // allocate a buffer for it in the buffer map
    public void initAttributeVariable( AttributeVariable av ) {
        this.buffers.put( av, new ArrayList<Object>() );
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
        for( int offset : ENUMERATION )
            this.indices.add( this.indexCount + offset );
        this.indexCount += 4;
        this.vertexCount += 6;
    }

    private void bind() {
        GL30.glBindVertexArray( this.vaoId );
    }

    private void unbind() {
        GL30.glBindVertexArray( 0 );
    }

    public void buildModel() {

        this.vaoId = GL30.glGenVertexArrays();

        // bind to the model
        this.bind();

        for( AttributeVariable av : BlockShader.GLOBAL.USED_ATTRIBUTE_VARS )
			GL20.glEnableVertexAttribArray( av.ordinal() );
        
        // create the vbo for the index data, load it into a buffer and push it
        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer ixBuf = intBufferFromCollection( this.indices );
        GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, ixBuf, GL15.GL_STATIC_DRAW );

        // keep track of the index VBO ids for cleanup later
        this.vboIds.add( vboId );

        // we can empty the index buffer now as the index data lives in VRAM
        this.indices.clear();

		for( AttributeVariable av : AttributeVariable.values()) {

            // grab the attribute variable buffer
            // atm we don't know if it holds ints or floats
            Collection<Object> buffer = this.buffers.get( av );
            if( buffer == null)
            	continue;

            // setup a new VBO
            vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

            // if the av is an integer, (and we hope the data put into the buffer is also that or else RIP)
            // create an intbuffer and push the data
            if( av.dataType == Integer.class ) {
                IntBuffer buf = intBufferFromCollection( buffer );
                GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
                GL30.glVertexAttribIPointer( av.ordinal(), av.stride, GL11.GL_INT, 0, 0  );
            // else do the same but with a float buffer
            } else if ( av.dataType == Float.class ) {
                FloatBuffer buf = floatBufferFromCollection( buffer );
                GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
                GL20.glVertexAttribPointer( av.ordinal(), av.stride, GL11.GL_FLOAT, false, 0, 0);
            }

            // keep track of the VBO ids for cleanup later
            this.vboIds.add( vboId );

            // we can empty the buffer as the data lives in VRAM now
            buffer.clear();
        }

        this.unbind();
    }

    // jump through and delete all VBOs
    // before deleting the VAO itself
    public void destroy() {

        this.bind();
        for( int vboId : this.vboIds)
            GL15.glDeleteBuffers(vboId);
        GL30.glDeleteVertexArrays(this.vaoId);
        this.unbind();

    }

    public void render() {

        // bind and activate the model's texture atlas
        GL13.glActiveTexture( GL13.GL_TEXTURE0 );
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, this.atlasId );

        // bind to the model's VAO
        this.bind();

        // draw the model!
        GL11.glDrawElements( GL11.GL_TRIANGLES, this.vertexCount, GL11.GL_UNSIGNED_INT, 0 );

        // unbind the VAO
        this.unbind();
    }

}
