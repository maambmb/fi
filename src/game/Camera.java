package game;

import org.lwjgl.util.vector.Matrix4f;

import game.component.Position3DComponent;
import game.gfx.shader.BlockShader;
import game.gfx.shader.Shader;

import util.MatrixUtils;

public class Camera extends Entity {

    public static Camera camera;
    public static void init() {
        camera = new Camera();
    }

    public Matrix4f matrixBuffer;
    private Position3DComponent posCmpt;

    private Camera() {
        super();
        this.matrixBuffer = new Matrix4f();
        this.globalListenerClient.addListener( BlockShader.BlockShaderPrepareMessage.class,
                (msg) -> this.shaderPrepare( BlockShader.SHADER ) );
    }

    private void loadProjectionMatrix( Shader s ) {
        Matrix4f.setIdentity(this.matrixBuffer);

        float aspectRatio    = Config.GAME_WIDTH / (float)Config.GAME_HEIGHT;
        float yScale         = (float) (aspectRatio / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW )));
        float xScale         = yScale / aspectRatio;
        float frustrumLength = Config.FAR_PLANE - Config.NEAR_PLANE;

        this.matrixBuffer.m00 = xScale;
        this.matrixBuffer.m11 = yScale;
        this.matrixBuffer.m22 = - (Config.FAR_PLANE + Config.NEAR_PLANE) / frustrumLength;
        this.matrixBuffer.m23 = -1;
        this.matrixBuffer.m32 = -2 * Config.NEAR_PLANE * Config.FAR_PLANE / frustrumLength;
        this.matrixBuffer.m33 = 0;
        s.loadProjectionMatrix( this.matrixBuffer );
    }
	
	private void loadViewMatrix( Shader s ) {
		Matrix4f.setIdentity(this.matrixBuffer);
        MatrixUtils.rotateMatrix( this.matrixBuffer, this.posCmpt.rotation );
        Matrix4f.translate( this.posCmpt.position.negate().toVector3f(), this.matrixBuffer, this.matrixBuffer );
        s.loadViewMatrix( this.matrixBuffer );
	}

    @Override
    public void addComponents() {
        this.posCmpt = new Position3DComponent();
        this.addComponent( this.posCmpt );
    }

    private void shaderPrepare( Shader s ) {
        this.loadViewMatrix( s );
        this.loadProjectionMatrix( s );
    } 

}
