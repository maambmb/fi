package game;

import org.lwjgl.util.vector.Matrix4f;

import game.component.NoClipComponent;
import game.component.Position3DComponent;
import game.gfx.UniformVariable;
import game.gfx.shader.BlockShader;
import game.gfx.shader.Shader;

import util.MatrixUtils;
import util.Vector3fl;

public final class Camera extends Entity {

    public static Camera camera;
    public static void init() {
        camera = new Camera();
    }

    private static Matrix4f matrixBuffer = new Matrix4f();

    private Position3DComponent posCmpt;

    private Camera() {
        super();
        this.globalListenerClient.addSubscriber( BlockShader.BlockShaderPrepareMessage.class,
            (msg) -> this.shaderPrepare( BlockShader.SHADER ) );
        this.setup();
    }

    private void loadProjectionMatrix( Shader s ) {

        float aspectRatio    = Config.GAME_WIDTH / (float)Config.GAME_HEIGHT;
        float yScale         = (float) (aspectRatio / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW )));
        float xScale         = yScale / aspectRatio;
        float frustrumLength = Config.FAR_PLANE - Config.NEAR_PLANE;

        Matrix4f.setIdentity(matrixBuffer);

        matrixBuffer.m00 = xScale;
        matrixBuffer.m11 = yScale;
        matrixBuffer.m22 = - (Config.FAR_PLANE + Config.NEAR_PLANE) / frustrumLength;
        matrixBuffer.m23 = -1;
        matrixBuffer.m32 = -2 * Config.NEAR_PLANE * Config.FAR_PLANE / frustrumLength;
        matrixBuffer.m33 = 0;

        s.loadMatrix4f( UniformVariable.PROJECTION_MATRIX, matrixBuffer );
    }

    private static Vector3fl viewBuffer = new Vector3fl(); 

    private void loadViewMatrix( Shader s ) {
        Matrix4f.setIdentity(matrixBuffer);

        Vector3fl.negate( viewBuffer, this.posCmpt.rotation );
        MatrixUtils.rotateMatrix( matrixBuffer, viewBuffer );
        Vector3fl.negate( viewBuffer, this.posCmpt.position );
        MatrixUtils.translateMatrix( matrixBuffer, viewBuffer );

        s.loadMatrix4f( UniformVariable.VIEW_MATRIX, matrixBuffer );
    }

    @Override
    public void addComponents() {
        this.posCmpt = new Position3DComponent();
        this.addComponent( this.posCmpt );
        this.addComponent( new NoClipComponent() );
    }

    private void shaderPrepare( Shader s ) {
        this.loadViewMatrix( s );
        this.loadProjectionMatrix( s );
    } 

}
