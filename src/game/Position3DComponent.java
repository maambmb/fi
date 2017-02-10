package game;


import util.Vector3fl;

public class Position3DComponent implements Component {

    public Vector3fl position;
    public Vector3fl rotation;
    public float scale;

    public Position3DComponent() { 
    	this.position = new Vector3fl();
    	this.rotation = new Vector3fl();
    	this.scale = 1f;
    }

    public void setup( Entity e ) {
    }

    @Override
    public void destroy() {
    }

	@Override
	public void init() {
		
	}

}
