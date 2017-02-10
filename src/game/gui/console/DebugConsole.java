package game.gui.console;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import game.Entity;
import game.Game.UpdateMessage;
import game.gfx.GlobalSubscriberComponent;
import game.gui.Glyph;
import game.gui.TextRenderComponent;
import game.input.InputCapturer;
import game.input.InputListenerComponent;
import game.input.InputPriority;
import game.input.Key;
import util.Vector3in;

public class DebugConsole extends Entity {

	public static DebugConsole GLOBAL;
	public static void init() {
		GLOBAL = new DebugConsole(400,10);
	}
	
	private List<Glyph> current;
	private LinkedList<List<Glyph>> history;
	private int cursor;
	
	private InputListenerComponent inputCmpt;
	private TextRenderComponent textCmpt;

	private int maxWidth;
	private boolean active;
	
	public DebugConsole( int maxWidth, int maxHeight ) {
		this.history = new LinkedList<List<Glyph>>();
		this.current = new ArrayList<Glyph>();
		this.maxWidth = maxWidth;
		this.cursor = 0;

		for( int i = 0; i < maxHeight; i += 1)
			this.history.add( new ArrayList<Glyph>() );
		
		this.listener.addSubscriber( UpdateMessage.class, this::update );
		this.refreshTextComponent();
	}

	private void newLine() {
		this.history.addLast( this.current );
		StringBuilder sb = new StringBuilder();
		for( Glyph g : this.current )
			if(g.underlying != Glyph.DEFAULT_CHAR)
				sb.append( g.underlying );
		DebugCommand.run( sb.toString() );
		this.current = new ArrayList<Glyph>();
		this.history.removeFirst();
		this.cursor = 0;
		this.refreshTextComponent();
	}
	
	private void addGlyph( Glyph g ) {
		if( this.current.size() < this.maxWidth) {
			this.current.add( this.cursor, g );
			this.cursor += 1;
			this.refreshTextComponent();
		}
	}
	
	public void log( String s ) {
		List<Glyph> glyphList = new ArrayList<Glyph>();
		for( char c : s.toCharArray() )
			glyphList.add( Glyph.lookup( c ) );
		this.history.removeFirst();
		this.history.addLast( glyphList );
		this.refreshTextComponent();
	}
	
	private void removeGlyph() {
		if( this.cursor > 0 && this.cursor <= this.current.size()) {
			this.current.remove( this.cursor - 1 );
			this.cursor -= 1;
			this.refreshTextComponent();
		}
	}
	
	private void refreshTextComponent() {
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
	
	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
		this.textCmpt = this.registerComponent( new TextRenderComponent() );
		this.inputCmpt = this.registerComponent( new InputListenerComponent( InputPriority.GUI_01 ) );
	}
	
	private void update( UpdateMessage m ) {
		
		if( !this.inputCmpt.canListen() )
			return;

		boolean upper  = InputCapturer.GLOBAL.isKeyDown( Key.KEY_LSHIFT );

		if( InputCapturer.GLOBAL.isKeyPressed( Key.KEY_RETURN ))
			this.newLine();
		else if( InputCapturer.GLOBAL.isKeyPressed( Key.KEY_BACKSPACE) )
			this.removeGlyph();

		for( Key k : InputCapturer.GLOBAL.getPressedKeys() ) {
			if( k.lowerGlyph == null || k == Key.KEY_GRAVE )
				continue;
			if( upper && k.upperGlyph != null )
				this.addGlyph( k.upperGlyph );
			else if( !upper && k.lowerGlyph != null )
				this.addGlyph( k.lowerGlyph );
		}

	}
	
	public void toggle() {
		if( this.active ) {
			this.textCmpt.visible = false;
			this.inputCmpt.stopListening();
		}
		else {
			this.textCmpt.visible = true;
			this.inputCmpt.startListening();
		}
		this.active = !this.active;
	}

}
