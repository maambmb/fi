package game.gfx;

import org.lwjgl.util.vector.Matrix4f;

import game.Component;
import game.Entity;
import game.Position3DComponent;
import game.gfx.shader.Shader;

import util.MatrixUtils;

public abstract class ModelRenderComponent implements Component {

    private static Matrix4f matrixBuffer = new Matrix4f();

    private Position3DComponent posCmpt;
    public Model model;

    public ModelRenderComponent() {
    }

    @Override
    public void setup( Entity e ) {
        // grab the position 3d cmpt from the entity
        e.listener.addSubscriber( Position3DComponent.class, x -> this.posCmpt = x );
    }

    // create a matrix which represents an entities rotation and shunt it to the shader
    private void loadRotateModelMatrix( Shader s ) {
        Matrix4f.setIdentity( matrixBuffer );
        MatrixUtils.addRotationToMatrix( matrixBuffer, this.posCmpt.rotation );
        s.loadMatrix4f( UniformVariable.MODEL_ROTATE_MATRIX, matrixBuffer );
    }

    // create a matrix which represents an entities scale + translation and shunt it to the shader
    private void loadTranslateScaleMatrix( Shader s ) {
        Matrix4f.setIdentity( matrixBuffer );
        MatrixUtils.addScaleToMatrix( matrixBuffer, this.posCmpt.scale );
        MatrixUtils.addTranslationToMatrix( matrixBuffer, this.posCmpt.position );
        s.loadMatrix4f( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX, matrixBuffer );
    }

    // render an entity using the model specified
    protected void shaderRender( Shader s ) {

        // if we have no model then we got nothing to do
        if( this.model == null )
            return;

        // first prepare the model matrices
        this.loadRotateModelMatrix( s );
        this.loadTranslateScaleMatrix( s );

        // then render the underlying model
        this.model.render();
    }
    
    public void init() {
    	
    }

}
