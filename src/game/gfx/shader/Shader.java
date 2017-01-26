package game.gfx.shader;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import game.block.LightSource;
import game.gfx.AttributeVariable;
import game.gfx.UniformVariable;

import util.FileUtils;
import util.Vector3fl;
import util.Vector3in;

public abstract class Shader {

    protected int programId;
    private int vertexId;
    private int fragmentId;
    private final FloatBuffer matrixFloatBuffer;

    private Map<UniformVariable,Integer> uniformLookup;

    public Shader( String vertex, String fragment ) {
        this.programId = GL20.glCreateProgram();
        this.matrixFloatBuffer = BufferUtils.createFloatBuffer(16);

        this.uniformLookup = new HashMap<UniformVariable,Integer>();
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
        GL20.glBindAttribLocation( this.programId, 0, "av_position" );
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
    protected void createUniformVariable( UniformVariable uv ) {
        int uvId = GL20.glGetUniformLocation( this.programId, uv.name );
        this.uniformLookup.put( uv, uvId );
    }

    // setup a new attribute list variable for the shader program at specified position
    protected void createAttributeVariable( AttributeVariable av ) {
        GL20.glBindAttribLocation( this.programId, av.ordinal(), av.name );
    }

    // load a 3d float vector into a uniform variable position
    protected void loadVector3fl( int pos, Vector3fl v ) {
        GL20.glUniform3f( pos, v.x, v.y, v.z);
    }

    // load a 3d int vector into a uniform variable position
    protected void loadVector3in( int pos, Vector3in v ) {
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
    protected abstract void setupVAOAttributes();

    // setup all uniform variables
    protected abstract void setupUniformVariables();

    public void loadViewMatrix( Matrix4f mat ) {
        int uvId = this.uniformLookup.get( UniformVariable.VIEW_MATRIX );
        this.loadMatrix4f( uvId, mat );
    }

    public void loadProjectionMatrix( Matrix4f mat ) {
        int uvId = this.uniformLookup.get( UniformVariable.PROJECTION_MATRIX );
        this.loadMatrix4f( uvId, mat );
    }

    public void loadModelRotateMatrix( Matrix4f mat ) {
        int uvId = this.uniformLookup.get( UniformVariable.MODEL_ROTATE_MATRIX );
        this.loadMatrix4f( uvId, mat );
    }

    public void loadModelTranslateScaleMatrix( Matrix4f mat ) {
        int uvId = this.uniformLookup.get( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX );
        this.loadMatrix4f( uvId, mat );
    }

    public void loadLighting( LightSource src, Vector3fl v ) {
        int uvId = this.uniformLookup.get( src.uniformVariable );
        this.loadVector3fl( uvId, v );
    }

    public void loadBaseLighting( Vector3fl v ) {
        int uvId = this.uniformLookup.get( UniformVariable.LIGHTING_BASE );
        this.loadVector3fl( uvId, v );
    }

    public void loadFogColor( Vector3fl v ) {
        int uvId = this.uniformLookup.get( UniformVariable.FOG_COLOR );
        this.loadVector3fl( uvId, v );
    }

    public void loadLightSource( Vector3fl v ) {
        int uvId = this.uniformLookup.get( UniformVariable.LIGHT_SOURCE );
        this.loadVector3fl( uvId, v );
    }

}
