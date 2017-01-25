package game.gfx.shader;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import util.FileUtils;
import util.Vector3f;
import util.Vector3i;

public class Shader {

	protected int programId;
	private int vertexId;
	private int fragmentId;
	private final FloatBuffer matrixFloatBuffer;
	
	private int viewMatrixId;
	private int projectionMatrixId;
	private int modelMatrixId;
	
	public Shader() {
		this.programId = GL20.glCreateProgram();
		this.matrixFloatBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void setup( String vertex, String fragment ) {
		// create a vertex and fragment shader
		this.vertexId = GL20.glCreateShader( GL20.GL_VERTEX_SHADER );
		this.fragmentId = GL20.glCreateShader( GL20.GL_FRAGMENT_SHADER );
		// load the source files for each shader
		GL20.glShaderSource( vertexId, FileUtils.readFile( vertex ) );
		GL20.glShaderSource( fragmentId, FileUtils.readFile( fragment ) );
		// compile and validate the shaders
		GL20.glCompileShader( this.vertexId );
		if( GL20.glGetShaderi( this.vertexId, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE )
        	throw new RuntimeException( String.format( "shader: '%s' couldn't be compiled", vertex ) );
		GL20.glCompileShader( this.fragmentId );
		if( GL20.glGetShaderi( this.fragmentId, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE )
        	throw new RuntimeException( String.format( "shader: '%s' couldn't be compiled", fragment ) );
		// attach and link the shaders to the shader program
		setupVAOAttributes();
		GL20.glAttachShader( this.programId, this.vertexId );
		GL20.glAttachShader( this.programId, this.fragmentId );
		GL20.glLinkProgram( this.programId );
		GL20.glValidateProgram( this.programId );
		// bind to the program and set up the uniform variables
		this.use();
		setupUniformVariables();
	}
	
	// allocate a new uniform variable for the shader program
	protected int createUniformVariable( String var ) {
		return GL20.glGetUniformLocation( this.programId, var );
	}

	// setup a new attribute list variable for the shader program at specified position
	protected void createAttributeListVariable( int position, String var ) {
		GL20.glBindAttribLocation( this.programId, position, var );
	}

	// load a 3d float vector into a uniform variable position
	protected void loadVector3f( int pos, Vector3f v ) {
		GL20.glUniform3f( pos, v.x, v.y, v.z);
	}

	// load a 3d int vector into a uniform variable position
	protected void loadVector3i( int pos, Vector3i v ) {
		GL20.glUniform3i( pos, v.x, v.y, v.z);
	}
	
	// load a 4d matrix into a uniform variable position
	protected void loadMatrix4f( int pos, Matrix4f m ) {
		m.store( this.matrixFloatBuffer );
		this.matrixFloatBuffer.flip();
		GL20.glUniformMatrix4( pos,  false,  this.matrixFloatBuffer);
	}
	
	// load a single int into a uniform variable position
	protected void loadInt( int pos, int i ) {
		GL20.glUniform1i(pos, i);
	}
	
	// bind to a shader program
	public void use() {
		GL20.glUseProgram( this.programId );
	}
	
	// setup all VAO variables
	protected void setupVAOAttributes() {
		this.createAttributeListVariable( 0, "ao_position" );
	}

	// setup all uniform variables
	protected void setupUniformVariables() {
		this.viewMatrixId = this.createUniformVariable( "uv_view_mat" );
		this.projectionMatrixId = this.createUniformVariable( "uv_proj_mat" );
		this.modelMatrixId = this.createUniformVariable( "uv_mod_mat" );
	}
	
	// load the view matrix
	public void loadViewMatrix( Matrix4f mat ) {
		this.loadMatrix4f( this.viewMatrixId, mat );
	}
	
	// load the projection matrix
	public void loadProjectionMatrix( Matrix4f mat ) {
		this.loadMatrix4f( this.projectionMatrixId, mat );
	}
	
	// load the model matrix
	public void loadModelMatrix( Matrix4f mat ) {
		this.loadMatrix4f( this.modelMatrixId, mat );
	}
	
	
}
