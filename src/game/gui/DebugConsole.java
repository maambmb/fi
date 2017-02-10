package game.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import game.Entity;
import game.Game.UpdateMessage;
import game.gfx.GlobalSubscriberComponent;
import game.input.Input;
import game.input.Input.Priority;
import game.input.InputListenerComponent;
import game.input.Key;
import util.Vector3in;

public class DebugConsole extends Entity {

	public static DebugConsole GLOBAL;
	public static void init() {
		GLOBAL = new DebugConsole(40,10);
	}
	
	private List<Glyph> current;
	private LinkedList<List<Glyph>> history;
	private int cursor;
	
	private InputListenerComponent inputCmpt;
	private TextRenderComponent textCmpt;

	private int maxWidth;
	private int maxHeight;
	
	public DebugConsole( int maxWidth, int maxHeight ) {
		this.history = new LinkedList<List<Glyph>>();
		this.current = new ArrayList<Glyph>();
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.cursor = 0;

		for( int i = 0; i < maxHeight; i += 1)
			this.history.add( new ArrayList<Glyph>() );
		
		this.listener.addSubscriber( UpdateMessage.class, this::update );
	}

	private void newLine() {
		this.history.addLast( this.current );
		this.current = new ArrayList<Glyph>();
		if( this.history.size() > this.maxHeight )
			this.history.removeFirst();
		this.cursor = 0;
	}
	
	private void addGlyph( Glyph g ) {
		if( this.current.size() < this.maxWidth) {
			this.current.add( this.cursor, g );
			this.cursor += 1;
		}
	}
	
	private void removeGlyph() {
		if( this.cursor > 0 && this.cursor <= this.current.size()) {
			this.current.remove( this.cursor - 1 );
			this.cursor -= 1;
		}
	}
	
	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
		this.textCmpt = this.registerComponent( new TextRenderComponent() );
		this.inputCmpt = this.registerComponent( new InputListenerComponent( Priority.GUI_01 ) );
		this.inputCmpt.startListening();
	}
	
	private void update( UpdateMessage m ) {
		
		if( this.inputCmpt.canListen() ) {

			boolean capital = Input.GLOBAL.isKeyDown( Key.KEY_LSHIFT );

			if( Input.GLOBAL.isKeyPressed( Key.KEY_RETURN ))
				this.newLine();
			else if( Input.GLOBAL.isKeyPressed( Key.KEY_BACKSPACE) )
				this.removeGlyph();

			for( Key k : Input.GLOBAL.getPressedKeys() )
				if( capital && k.upperGlyph != null )
					this.addGlyph( k.upperGlyph );
				else if( !capital && k.lowerGlyph != null )
					this.addGlyph( k.lowerGlyph );
		}
		
		this.textCmpt.reset();
		for( List<Glyph> glyphs : this.history ) {
			for( Glyph g : glyphs )
				this.textCmpt.addGlyph( g, Vector3in.GREEN );
			this.textCmpt.newline();
		}
		for( Glyph g : this.current ) {
			this.textCmpt.addGlyph( g, Vector3in.GREEN );
		}
		this.textCmpt.addGlyph( Glyph.BLOCK, Vector3in.GREEN );
	}

}
