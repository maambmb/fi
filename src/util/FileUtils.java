package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileUtils {

	public static String readFile( String path ) {
		StringBuilder str = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			while( true ) {
				String line = reader.readLine();
				if( line == null ) {
					reader.close();
					break;
				}
				str.append(line).append("\n");
			}
			return str.toString();
		} catch( IOException e) {
			e.printStackTrace();
			throw new RuntimeException( String.format( "something went wrong reading: %s", path ) );
		}
	}
	
}
