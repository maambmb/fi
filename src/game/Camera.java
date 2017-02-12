package game;

import game.block.BlockShader;
import game.env.Environment;
import game.gfx.GlobalSubscriberComponent;
import game.gfx.Shader;
import game.gfx.UniformVariable;
import game.input.InputListenerComponent;
import game.input.InputPriority;
import game.input.NoClipComponent;
import util.Matrix4fl;

public final class Camera extends Entity {

    public static Camera GLOBAL;
    private static Matrix4fl matrix = new Matrix4fl();

    public static void init() {
        GLOBAL = new Camera();
    }

    private Position3DComponent posCmpt;

    private Camera() {
        super();
        this.listener.addSubscriber( BlockShader.BlockShaderPreRenderMessage.class, this::blockShaderPreRender );
    }

    private void loadViewMatrix( Shader s ) {

    	matrix.clearMatrix();
    	matrix.addPitchToMatrix( this.posCmpt.rotation.x * - 1 );
    	matrix.addYawToMatrix( this.posCmpt.rotation.y * - 1 );
        matrix.addTranslationToMatrix( this.posCmpt.position.multiply(-1) );
        s.loadMatrix4f( UniformVariable.VIEW_MATRIX, matrix );
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
        this.loadProjectionMatrix( s );
    } 
    
    private void blockShaderPreRender( BlockShader.BlockShaderPreRenderMessage msg ) {
    	this.preRender( BlockShader.GLOBAL );
    }

    private void loadProjectionMatrix( Shader s ) {

        float yScale = (float) (Config.ASPECT_RATIO / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW / 2f )));
        float xScale = yScale / Config.ASPECT_RATIO;
        float frustrumLength = Environment.GLOBAL.maxDistance - Config.NEAR_PLANE;

        matrix.clearMatrix();

        matrix.raw.m00 = xScale;
        matrix.raw.m11 = yScale;
        matrix.raw.m22 = - (Environment.GLOBAL.maxDistance + Config.NEAR_PLANE) / frustrumLength;
        matrix.raw.m23 = -1;
        matrix.raw.m32 = -2 * Config.NEAR_PLANE * Environment.GLOBAL.maxDistance / frustrumLength;
        matrix.raw.m33 = 0;

        s.loadMatrix4f( UniformVariable.PROJECTION_MATRIX, matrix );
    }
    
}
