package game.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

import game.Component;
import game.Entity;
import game.gfx.UniformVariable;
import game.gui.GUIShader.GUIShaderRenderMessage;
import util.Matrix4fl;
import util.Tuple;
import util.Vector3fl;
import util.Vector3in;

public class TextRenderComponent implements Component {

	
	public FontMap fontMap;
	public float fontSize;
	public GUIDepth depth;
	public boolean visible;
	public Vector3fl position;
	private List<Tuple.Binary<Glyph,Vector3in>> glyphs;

	private static Matrix4fl matrix = new Matrix4fl();
	
	public TextRenderComponent( ) {
		this.fontMap = FontMap.DEBUG;
		this.depth = GUIDepth.DEPTH_0;
		this.fontSize = 2f;
		this.position = new Vector3fl();
		this.glyphs = new ArrayList<Tuple.Binary<Glyph,Vector3in>>();
	}
	
    @Override
    public void setup( Entity e ) {
        // only render when the block shader program is active
        e.listener.addSubscriber( GUIShader.GUIShaderRenderMessage.class, this::render );
    }
    
    private void render( GUIShaderRenderMessage msg ) {

    	if( msg.depth != this.depth || !this.visible )
    		return;
    	
    	int xCur = 0;
    	int yCur = 0;

    	Vector3in dims = this.fontMap.dimensions;
    	for( Tuple.Binary<Glyph, Vector3in> glyph : this.glyphs ) {
    		
    		if( glyph.arg1 == Glyph.NEWLINE ) { 
    			yCur += 1;
    			xCur = 0;
    			continue;
    		}
    		
			matrix.clearMatrix();
			matrix.addTranslationToMatrix( this.position );
			matrix.addScaleToMatrix( this.fontSize );
			matrix.addTranslationToMatrix( new Vector3fl( xCur * dims.x, yCur * dims.y ) );
			GUIShader.GLOBAL.loadMatrix4f( UniformVariable.MODEL, matrix );
			GUIShader.GLOBAL.loadInt( UniformVariable.COLOR, glyph.arg2.toPackedBytes() );
			this.fontMap.getModel( glyph.arg1 ).render();

			xCur += 1;
    	}
    }
    
    public void reset() {
    	this.glyphs.clear();
    }
    
    public void addGlyph( Glyph g, Vector3in color ) {
    	this.glyphs.add( Tuple.create( g, color ) );
    }
    
    public void newline() {
    	this.glyphs.add( Tuple.create( Glyph.NEWLINE, Vector3in.WHITE ) );
    }
    
    public void addString( String s, Vector3in color ) {
    	for( char c : s.toCharArray() )
    		this.addGlyph( Glyph.lookup( c ), color );
    }

    @Override
    public void destroy() {
    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
