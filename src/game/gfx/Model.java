package game.gfx;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Model {

    private List<Integer> vboIds;
    private List<AttributeVariable> usedAttribVariables;
    private int vaoId;
    private int vertexCount;
    public int atlasId;

    public Model( int vertexCount ) {
        this.vaoId               = GL30.glGenVertexArrays();
        this.vertexCount         = vertexCount;
        this.vboIds              = new ArrayList<Integer>();
        this.usedAttribVariables = new ArrayList<AttributeVariable>();
    }

    private void bind() {
        GL30.glBindVertexArray( this.vaoId );
    }


    public void addPositionData( Collection<Float> floatData ) {
        this.bind();

        FloatBuffer buf = BufferUtils.createFloatBuffer( this.vertexCount * 3 );
        for( float f : floatData )
            buf.put( f );
        buf.flip();

        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
        GL20.glVertexAttribPointer( 0, 3, GL11.GL_FLOAT, false, 0, 0);
        this.vboIds.add( vboId );
    }

    public void addIndexData( Collection<Integer> intData ) {
        this.bind();

        IntBuffer buf = BufferUtils.createIntBuffer( this.vertexCount );
        for( int i : intData )
            buf.put( i );
        buf.flip();

        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
        this.vboIds.add( vboId );
    }

    public void addAttributeData( AttributeVariable iv, Collection<Integer> intData ) {
        this.bind();

        IntBuffer buf = BufferUtils.createIntBuffer( this.vertexCount );
        for( int i : intData )
            buf.put( i );
        buf.flip();

        int vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData( GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW );
        GL20.glVertexAttribPointer( iv.ordinal() + 1, 1, GL11.GL_INT, false, 0, 0);
        this.usedAttribVariables.add( iv );
        this.vboIds.add( vboId );
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
        GL20.glEnableVertexAttribArray( 0 );
        for( AttributeVariable av : this.usedAttribVariables )
            GL20.glEnableVertexAttribArray( av.ordinal() + 1 );
        // draw the model!
        GL11.glDrawElements( GL11.GL_TRIANGLES, this.vertexCount, GL11.GL_UNSIGNED_INT, 0 );
        // disable all the attribute variables used
        GL20.glDisableVertexAttribArray( 0 );
        for( AttributeVariable av : this.usedAttribVariables )
            GL20.glDisableVertexAttribArray( av.ordinal() + 1 );
        // unbind the VAO
        GL30.glBindVertexArray( this.vaoId );
    }

}
