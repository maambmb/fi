package game.gui.console;

import game.env.Environment;
import util.Lambda;
import util.Vector3in;

public enum DebugCommand {

	SET_FOG_COLOR( 
		"/setfogcolor", (args) -> {
			int decoded = Integer.decode( args[1] );
			Environment.GLOBAL.fogColor = new Vector3in( decoded );
			DebugConsole.GLOBAL.log( String.format( "Updated fog color to: %X", decoded) );
		}
	),
	SET_BASE_LIGHTING(
		"/setbaselighting", (args) -> {
			int decoded = Integer.decode( args[1] );
			Environment.GLOBAL.baseLighting = new Vector3in( decoded );
			DebugConsole.GLOBAL.log( String.format( "Updated base lighting to: %X", decoded) );
		}
	);
	
	private String prefix;
	private Lambda.ActionUnary<String[]> action;
	private DebugCommand( String prefix, Lambda.ActionUnary<String[]> action ) {
		this.prefix = prefix;
		this.action = action;
	}
	
	public static void run( String cmd ) {
		String[] split = cmd.split(" ");
		for( DebugCommand dc : DebugCommand.values() ) {
			if( dc.prefix.equals( split[0].toLowerCase() ) ) {
				dc.action.run( split );
				return;
			}
		}
	}

	
}
