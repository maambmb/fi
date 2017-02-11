package game;

import org.lwjgl.util.vector.Matrix4f;

import game.block.BlockShader;
import game.gfx.GlobalSubscriberComponent;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import game.input.InputListenerComponent;
import game.input.InputPriority;
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
        InputListenerComponent inputCmpt = this.registerComponent( new InputListenerComponent( InputPriority.CONTROL ));
        inputCmpt.startListening();
    }

    private void preRender( Shader s ) {
        this.loadViewMatrix( s );
    } 
    
    private void blockShaderPreRender( BlockShader.BlockShaderPreRenderMessage msg ) {
    	this.preRender( BlockShader.GLOBAL );
    }
    
}
