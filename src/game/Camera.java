package game;

import org.lwjgl.util.vector.Matrix4f;

import game.component.Position3DComponent;

import util.MatrixUtils;

public class Camera extends Entity {

    public static Camera camera;
    public static void init() {
        camera = new Camera();
    }

    public Matrix4f projectionMatrix;
    public Matrix4f viewMatrix;
    private Position3DComponent posCmpt;

    private Camera() {
        super();

        this.viewMatrix       = new Matrix4f();
        this.projectionMatrix = new Matrix4f();

        this.setProjectionMatrix();
        this.globalListenerClient.addListener( Game.UpdateMessage.class, this::update );
    }

    private void setProjectionMatrix() {
		float aspectRatio = Config.GAME_WIDTH / (float)Config.GAME_HEIGHT;
		float yScale = (float) (aspectRatio / Math.tan( Math.toRadians( Config.FIELD_OF_VIEW )));
		float xScale = yScale / aspectRatio;
		Matrix4f.setIdentity(this.projectionMatrix);
		float frustrumLength      = Config.FAR_PLANE - Config.NEAR_PLANE;
		this.projectionMatrix.m00 = xScale;
		this.projectionMatrix.m11 = yScale;
		this.projectionMatrix.m22 = - (Config.FAR_PLANE + Config.NEAR_PLANE) / frustrumLength;
		this.projectionMatrix.m23 = -1;
		this.projectionMatrix.m32 = -2 * Config.NEAR_PLANE * Config.FAR_PLANE / frustrumLength;
		this.projectionMatrix.m33 = 0;
    }
	
	private void setViewMatrix() {
		Matrix4f.setIdentity(this.viewMatrix);
        MatrixUtils.rotateMatrix( this.viewMatrix, this.posCmpt.rotation );
        Matrix4f.translate( this.posCmpt.position.negate().toVector3f(), this.viewMatrix, this.viewMatrix );
	}

    @Override
    public void addComponents() {
        this.posCmpt = new Position3DComponent();
        this.addComponent( this.posCmpt );
    }

    public void update( Game.UpdateMessage msg ) {
        this.setViewMatrix(); 
    }

}
