package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import game.Game.UpdateMessage;
import game.component.GlobalSubscriberComponent;
import game.component.InputListenerComponent;

public class InputArbiter extends Entity {

	public enum Priority {
		
		TOP_LEVEL_LISTENER( false ),
		GUI_01( true ),
		CONTROL( true );
		
		private Priority( boolean capturing ) {
			this.capturing = capturing;
		}
		
		public boolean capturing;
		
	}
	
	public static InputArbiter GLOBAL;
	public static void init() {
		GLOBAL = new InputArbiter();
	}
	
	private Map<Priority,Set<InputListenerComponent>> inputListenerMap;
	private boolean[] prevDown;
	private boolean[] currDown;
	private List<Integer> pressedKeys;
	
	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
	}
	
	private void update( UpdateMessage m ){

		this.pressedKeys.clear();
		for( int i = 0; i < Keyboard.KEYBOARD_SIZE; i +=1 )
			this.prevDown[ i ] = this.currDown[ i ];

		while( Keyboard.next() ) {
			int key = Keyboard.getEventKey();
			boolean isDown = Keyboard.getEventKeyState();
			this.currDown[ key ] = isDown;
			if( isDown && ! this.prevDown[ key ] )
				this.pressedKeys.add( key );
		}
		
	}
	
	public InputArbiter() {
		this.inputListenerMap = new HashMap<Priority,Set<InputListenerComponent>>();
		for( Priority il : Priority.values())
			this.inputListenerMap.put( il , new HashSet<InputListenerComponent>() );
		this.prevDown = new boolean[ Keyboard.KEYBOARD_SIZE ];
		this.currDown = new boolean[ Keyboard.KEYBOARD_SIZE ];
		this.pressedKeys = new ArrayList<Integer>();
		this.listener.addSubscriber( UpdateMessage.class, this::update );
	}
	
	public void addListener( InputListenerComponent cmpt ) {
		this.inputListenerMap.get( cmpt.priority ).add( cmpt );
	}
	
	public void removeListener( InputListenerComponent cmpt ) {
		this.inputListenerMap.get( cmpt.priority ).remove( cmpt );
	}
	
	public boolean canListen( InputListenerComponent cmpt ) {
		for( Priority il : Priority.values() ) {
			if( il == cmpt.priority )
				return this.inputListenerMap.get( il ).contains( cmpt );
			else if( il.capturing && this.inputListenerMap.get( il ).size() > 0 )
				return false;
		}
		return false;
	}
	
	public Iterator<Integer> getPressedKeys() {
		return this.pressedKeys.iterator();
	}
	
	public boolean isKeyDown( int keyCode ) {
		return this.currDown[ keyCode ];
	}

	public boolean isPressed( int keyCode ) {
		return this.pressedKeys.contains( keyCode );
	}
	

}
