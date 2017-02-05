package game.input;

import game.Component;
import game.Entity;
import game.Game.UpdateMessage;

public class TextBufferComponent implements Component {

	private int[][] lineBuffers;
	private int width;
	private int height;
	private int numLines;
	
	private InputListenerComponent inputCmpt;
	
	public int curX;
	public int curY;

	public TextBufferComponent( int width, int height ) {
		this.lineBuffers = new int[ height ][ width ];
		this.width = width;
		this.height = height;
		this.numLines = numLines - 1;
		this.curX = 0;
		this.curY = height - 1;
	}
	
	public void newLine() {
		int[] toClear = this.lineBuffers[ numLines % this.height ];
		for( int i = 0; i < this.width; i += 1)
			toClear[i] = ' ';
		this.numLines += 1;
		this.curY += 1;
		this.curY = 0;
	}
	
	public void moveCursorUp() {
		if( this.curY > this.numLines - 1 - this.height )
			this.curY -= 1;
	}

	public void moveCursorDown() {
		if( this.curY < this.numLines - 1 )
			this.curY += 1;
	}
	
	public void moveCursorLeft() {
		if( this.curX > 0 )
			this.curX -= 1;
	}
	
	public void moveCursorRight() {
		if( this.curX < this.width - 1 )
			this.curX += 1;
	}
	
	public void write( char c ) {
		this.lineBuffers[ this.curY % this.height ][ this.curX ] = c;
		this.moveCursorRight();
	}

	@Override
	public void setup(Entity e) {
		e.listener.addSubscriber(InputListenerComponent.class, x -> this.inputCmpt = x );
		e.listener.addSubscriber(UpdateMessage.class, this::update);
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	private void update( UpdateMessage m ) {
		
		if( !this.inputCmpt.canListen() )
			return;
		
		boolean useCapital = this.inputCmpt.isKeyDown( Key.KEY_LSHIFT );
		for( Key k : this.inputCmpt.getPressedKeys() ) {
			if( k == Key.KEY_UP )
				this.curY = Math.max( this.curY - 1, 0 );
			else if( k == Key.KEY_DOWN )
				this.curY = Math.min( this.curY + 1, this.height - 1 );
			else if( k == Key.KEY_LEFT )
				this.curX = Math.max( this.curX - 1, 0 );
			else if( k == Key.KEY_RIGHT )
				this.curY = Math.min( this.curX + 1, this.width - 1 );
			else if( k.lowerCharacter == Key.DEFAULT_CHAR )
				continue;

			if( useCapital && k.upperCharacter != Key.DEFAULT_CHAR )
				this.write( k.upperCharacter );
			else
				this.write( k.lowerCharacter );
		}
	}
	
}
