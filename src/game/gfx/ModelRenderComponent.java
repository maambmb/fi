package game.gfx;

import org.lwjgl.util.vector.Matrix4f;

import game.Component;
import game.Entity;
import util.MatrixUtils;
import util.Vector3fl;

public abstract class ModelRenderComponent implements Component {

    private static Matrix4f matrixBuffer = new Matrix4f();

    public Model model;

    @Override
    public void setup( Entity e ) {
    }

    // create a matrix which represents an entities rotation and shunt it to the shader
    protected void loadRotateModelMatrix( Shader s, Vector3fl rot ) {
        Matrix4f.setIdentity( matrixBuffer );
        MatrixUtils.addRotationToMatrix( matrixBuffer, rot );
        s.loadMatrix4f( UniformVariable.MODEL_ROTATE_MATRIX, matrixBuffer );
    }

    // create a matrix which represents an entities scale + translation and shunt it to the shader
    protected void loadTranslateScaleMatrix( Shader s, Vector3fl pos, float scale ) {
        Matrix4f.setIdentity( matrixBuffer );
        MatrixUtils.addScaleToMatrix( matrixBuffer, scale );
        MatrixUtils.addTranslationToMatrix( matrixBuffer, pos );
        s.loadMatrix4f( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX, matrixBuffer );
    }

    public void init() {
    	
    }

}
