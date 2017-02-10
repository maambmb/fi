package game.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import game.Config;
import game.Entity;
import game.Game.UpdateMessage;
import game.gfx.GlobalSubscriberComponent;

public class Input extends Entity {

	public static Input GLOBAL;
	public static void init() {
		GLOBAL = new Input();
	}
	
	private Map<InputPriority,Set<InputListenerComponent>> inputListenerMap;
	private boolean[] prevDown;
	private boolean[] currDown;
	private long[] lastPressed;
	private List<Key> pressedKeys;
	private long currTime;
	
	@Override
	protected void registerComponents() {
		this.registerComponent( new GlobalSubscriberComponent() );
	}
	
	private void update( UpdateMessage m ){
		
		this.currTime = m.totalMs;
		this.pressedKeys.clear();
		
		for( Key key : Key.values() ) {

			int ix = key.ordinal();
			this.prevDown[ ix ] = this.currDown[ ix ];
			this.currDown[ ix ] = Keyboard.isKeyDown( key.keyCode );
			if( this.currDown[ ix ] ) {
				if( ! this.prevDown[ ix ] ) {
					this.pressedKeys.add( key );
					this.lastPressed[ ix ] = this.currTime;
				}
				else if( this.lastPressed[ ix ] + Config.KEY_LOCK_MS < this.currTime )
					this.pressedKeys.add( key );
			}
			
		}

	}
	
	public Input() {
		this.inputListenerMap = new HashMap<InputPriority,Set<InputListenerComponent>>();
		for( InputPriority il : InputPriority.values())
			this.inputListenerMap.put( il , new HashSet<InputListenerComponent>() );
		this.prevDown = new boolean[ Key.values().length ];
		this.currDown = new boolean[ Key.values().length ];
		this.lastPressed = new long[ Key.values().length ];
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
		for( InputPriority il : InputPriority.values() ) {
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
	
	public boolean isKeyPressed( Key key ) {
		return this.pressedKeys.contains( key );
	}

}
