package game;

import org.lwjgl.util.vector.Matrix4f;

import game.gfx.GlobalSubscriberComponent;
import game.gfx.UniformVariable;
import game.gfx.shader.BlockShader;
import game.gfx.shader.Shader;
import game.input.InputArbiter;
import game.input.InputListenerComponent;
import game.input.NoClipComponent;
import util.MatrixUtils;

public final class Camera extends Entity {

    public static Camera GLOBAL;
    public static void init() {
        GLOBAL = new Camera();
    }

    private static Matrix4f matrixBuffer = new Matrix4f();

    private Position3DComponent posCmpt;

    private Camera() {
        super();
        this.listener.addSubscriber( BlockShader.BlockShaderPreRenderMessage.class, this::blockShaderPreRender );
    }

    private void loadProjectionMatrix( Shader s ) {

        float aspectRatio    = Config.GAME_WIDTH / (float)Config.GAME_HEIGHT;
        float yScale         = (float) (aspectRatio / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW / 2f )));
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

    private void loadViewMatrix( Shader s ) {

        Matrix4f.setIdentity(matrixBuffer);
        MatrixUtils.addRotationMatrixReversed( matrixBuffer, this.posCmpt.rotation.multiply(-1) );
        MatrixUtils.addTranslationToMatrix( matrixBuffer , this.posCmpt.position.multiply(-1) );
        s.loadMatrix4f( UniformVariable.VIEW_MATRIX, matrixBuffer );
    }

    @Override
    public void registerComponents() {
        this.posCmpt = this.registerComponent( new Position3DComponent() );
        this.registerComponent( new NoClipComponent() );
        this.registerComponent( new GlobalSubscriberComponent() );
        InputListenerComponent inputCmpt = this.registerComponent( new InputListenerComponent( InputArbiter.Priority.CONTROL ));
        inputCmpt.startListening();
    }

    private void preRender( Shader s ) {
        this.loadViewMatrix( s );
        this.loadProjectionMatrix( s );
    } 
    
    private void blockShaderPreRender( BlockShader.BlockShaderPreRenderMessage msg ) {
    	this.preRender( BlockShader.GLOBAL );
    }

}
