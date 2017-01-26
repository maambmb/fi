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

    private static Object MUTEX = new Object();

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

    // given four vertices of a quad, this is the order in which their indices should
    // be specified in the index VBO
    private static final int[] QUAD_ENUMERATION = new int[] { 2, 1, 0, 3, 2, 0 };

    // count of the number of vertices
    private int index;
    // the index VBO buffer
    private List<Object> indices;
    // attribute variable buffers
    private Map<AttributeVariable,List<Object>> buffers;
    // a list of used VBOs
    private List<Integer> vboIds;
    // the VAO id
    private int vaoId;
    // the texture atlas id
    public int atlasId;

    public Model() {
        this.index   = 0;
        this.indices = new ArrayList<Object>();
        this.buffers = new HashMap<AttributeVariable,List<Object>>();
        this.vboIds  = new ArrayList<Integer>(); 
    }

    public void initAttributeVariable( AttributeVariable av ) {
            this.buffers.put( av, new ArrayList<Object>() );
    }

    public void addAttributeData( AttributeVariable av, int val ) {
        this.buffers.get( av ).add( val );
    }

    public void addAttributeData( AttributeVariable av, float val ) {
        this.buffers.get( av ).add( val );
    }

    public void addAttributeData( AttributeVariable av, boolean val ) {
        this.buffers.get( av ).add( val );
    }

    public void addAttributeData( AttributeVariable av, Vector3fl val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
        buffer.add( val.z );
    }

    public void addAttributeData2D( AttributeVariable av, Vector3fl val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
    }

    public void addAttributeData( AttributeVariable av, Vector3in val ) {
        List<Object> buffer = this.buffers.get( av );
        buffer.add( val.x );
        buffer.add( val.y );
        buffer.add( val.z );
    }

    // commit a quad to the builder (run after 4 vertices have been committed )
    // by adding their indices to the index buffer
    public void addQuad() {
        for( int offset : QUAD_ENUMERATION )
            this.indices.add( this.index + offset );
        this.index += 4;
    }

    private void bind() {
        GL30.glBindVertexArray( this.vaoId );
    }

    // create a model from the builder by ferrying the data into VRAM using OpenGL
    public void buildModel() {

        synchronized( MUTEX ) {
            this.bind();
            int vboId = GL15.glGenBuffers();
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
            IntBuffer ixBuf = intBufferFromCollection( this.indices );
            GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, ixBuf, GL15.GL_STATIC_DRAW );
            this.vboIds.add( vboId );
            this.indices.clear();

            for( AttributeVariable av : this.buffers.keySet() ) {

                Collection<Object> buffer = this.buffers.get( av );
                vboId = GL15.glGenBuffers();
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
                
                if( av.dataType == Integer.class ) {
                    IntBuffer buf = intBufferFromCollection( buffer );
                    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
                    GL20.glVertexAttribPointer( av.ordinal(), av.stride, GL11.GL_INT, false, 0, 0);
                } else if ( av.dataType == Float.class ) {
                    FloatBuffer buf = floatBufferFromCollection( buffer );
                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
                    GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
                }

                this.vboIds.add( vboId );
                buffer.clear();
            }
        }
    }

    public void destroy() {
        this.bind();
        for( int vboId : this.vboIds)
            GL15.glDeleteBuffers(vboId);
        GL30.glDeleteVertexArrays(this.vaoId);
    }

    public void render() {
        this.bind();

        // bind and activate the model's texture atlas
        GL13.glActiveTexture( GL13.GL_TEXTURE0 );
        GL11.glBindTexture( GL11.GL_TEXTURE_2D, this.atlasId );
        // bind to the model's VAO
        GL30.glBindVertexArray( this.vaoId );
        // enable all the attribute variables used
        for( AttributeVariable av : this.buffers.keySet() )
            GL20.glEnableVertexAttribArray( av.ordinal() );
        // draw the model!
        GL11.glDrawElements( GL11.GL_TRIANGLES, this.index, GL11.GL_UNSIGNED_INT, 0 );
        // disable all the attribute variables used
        for( AttributeVariable av : this.buffers.keySet() )
            GL20.glDisableVertexAttribArray( av.ordinal() );
        // unbind the VAO
        GL30.glBindVertexArray( this.vaoId );
    }

}
