package game.component;

import org.lwjgl.util.vector.Matrix4f;

import game.Entity;
import game.gfx.Model;
import game.gfx.shader.Shader;

import util.MatrixUtils;

public abstract class ModelRenderComponent implements Component {

    private Matrix4f matrixBuffer;
    private Position3DComponent posCmpt;
    public Model model;

    public ModelRenderComponent() {
        this.matrixBuffer = new Matrix4f();
    }

    @Override
    public void setup( Entity e ) {
        e.listener.addListener( Position3DComponent.class, x -> this.posCmpt = x );
    }

    private void loadRotateModelMatrix( Shader s ) {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.rotateMatrix( this.matrixBuffer, this.posCmpt.rotation );
        s.loadModelRotateMatrix( this.matrixBuffer );
    }

    private void loadTranslateScaleMatrix( Shader s ) {
        Matrix4f.setIdentity( this.matrixBuffer );
        MatrixUtils.translateMatrix( this.matrixBuffer, this.posCmpt.position );
        MatrixUtils.scaleMatrix( this.matrixBuffer, this.posCmpt.scale );
        s.loadModelTranslateScaleMatrix( this.matrixBuffer );
    }

    protected void shaderRender( Shader s ) {

        if( this.model == null )
            return;

        this.loadRotateModelMatrix( s );
        this.loadTranslateScaleMatrix( s );
        this.model.render();
    }

}
