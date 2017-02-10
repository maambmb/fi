package game.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import game.Entity;
import game.Game.UpdateMessage;
import game.gfx.GlobalSubscriberComponent;

public class InputArbiter extends Entity {

	public enum Priority {
		
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
	private List<Key> pressedKeys;
	
	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
	}
	
	private void update( UpdateMessage m ){

		this.pressedKeys.clear();
		for( int i = 0; i < Key.values().length; i +=1 )
			this.prevDown[ i ] = this.currDown[ i ];

		while( Keyboard.next() ) {
			Key key = Key.fromKeyCode( Keyboard.getEventKey() );
			boolean isDown = Keyboard.getEventKeyState();
			this.currDown[ key.ordinal() ] = isDown;
			if( isDown && ! this.prevDown[ key.ordinal() ] )
				this.pressedKeys.add( key );
		}

	}
	
	public InputArbiter() {
		this.inputListenerMap = new HashMap<Priority,Set<InputListenerComponent>>();
		for( Priority il : Priority.values())
			this.inputListenerMap.put( il , new HashSet<InputListenerComponent>() );
		this.prevDown = new boolean[ Key.values().length ];
		this.currDown = new boolean[ Key.values().length ];
		this.pressedKeys = new ArrayList<Key>();
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
	
	public Iterable<Key> getPressedKeys() {
		return this.pressedKeys;
	}
	
	public boolean isKeyDown( Key key ) {
		return this.currDown[ key.ordinal() ];
	}

	public boolean isPressed( Key key ) {
		return this.pressedKeys.contains( key );
	}
	

}
