package game.component;

import org.lwjgl.util.vector.Matrix4f;

import game.Entity;
import game.gfx.Model;
import game.gfx.UniformVariable;
import game.gfx.shader.Shader;

import util.MatrixUtils;

public abstract class ModelRenderComponent implements Component {

    private Matrix4f matrixBuffer;
    private Position3DComponent posCmpt;
    public Model model;

    public ModelRenderComponent() {
        // use this buffer for doing matrix manipulations
        this.matrixBuffer = new Matrix4f();
    }

    @Override
    public void setup( Entity e ) {
        // grab the position 3d cmpt from the entity
        e.listener.addSubcriber( Position3DComponent.class, x -> this.posCmpt = x );
    }

    // create a matrix which represents an entities rotation and shunt it to the shader
    private void loadRotateModelMatrix( Shader s ) {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.rotateMatrix( this.matrixBuffer, this.posCmpt.rotation );
        s.loadMatrix4f( UniformVariable.MODEL_ROTATE_MATRIX, this.matrixBuffer );
    }

    // create a matrix which represents an entities scale + translation and shunt it to the shader
    private void loadTranslateScaleMatrix( Shader s ) {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.translateMatrix( this.matrixBuffer, this.posCmpt.position );
        MatrixUtils.scaleMatrix( this.matrixBuffer, this.posCmpt.scale );
        s.loadMatrix4f( UniformVariable.MODEL_TRANSLATE_SCALE_MATRIX, this.matrixBuffer );
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

}
