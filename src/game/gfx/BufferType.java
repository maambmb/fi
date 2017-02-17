package game.gfx;

import org.lwjgl.opengl.GL15;

public enum BufferType {
	STATIC(GL15.GL_STATIC_DRAW),
	STREAM(GL15.GL_STREAM_DRAW);
	
	public int bufferType;
	private BufferType(int bufferType ) {
		this.bufferType = bufferType;
	}
}
