package game.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import game.Config;
import util.Pool;
import util.Vector3i;

public class Generator {

	public static class ModelFaceIlluminationData {

		public Vector3i illumination;
		public float globalIllumination;
		
		private ModelFaceIlluminationData() {
			this.illumination = new Vector3i();
			this.globalIllumination = 0;
		}

		public void reset() {
			this.globalIllumination = 0;
			this.illumination.reset();
		}

	}

	public static class ModelBlockData implements Pool.Poolable {
		
		private static Pool<ModelBlockData> POOL = new Pool<ModelBlockData>( ModelBlockData::new );

		public Block block;
		public Map<Vector3i.Normal,ModelFaceIlluminationData> faceData;

		private ModelBlockData() {
			for( Vector3i.Normal normal : Vector3i.Normal.NORMALS)
			this.faceData.put( normal, new ModelFaceIlluminationData() );
		}

		@Override
		public void destroy() {
			POOL.reclaim(this);
		}

		@Override
		public void init() {
			for( ModelFaceIlluminationData fd : this.faceData.values() )
				fd.reset();
		}
	}
	
	private Set<Vector3i> loadedChunks;
	private Map<Vector3i,ModelBlockData> modelMap;
	
	public Generator() {
		this.modelMap = new HashMap<Vector3i,ModelBlockData>();
		this.loadedChunks = new HashSet<Vector3i>();
	}
	
	private void loadBlock( Chunk c, Vector3i v) {
		ModelBlockData mdb = ModelBlockData.POOL.fresh();
		mdb.block = c.getBlock( v );
		modelMap.put( v, mdb );
	}
	
	public void loadChunk( World w, Vector3i chunkVec ) {
		if( this.loadedChunks.contains( chunkVec ) )
			return;
		
		Vector3i workingVec = new Vector3i();
		// load in padding chunks
		for( Vector3i.Normal normal : Vector3i.Normal.NORMALS )
			this.loadChunk( w, workingVec.set( chunkVec ).add( normal.vector ) );

		Chunk chunk = w.getChunk( chunkVec );
		if( chunk != null ) {
			Vector3i scaledVec = chunkVec.clone().multiply( Config.CHUNK_DIM );
			chunk.iterateBlocks( (c,v) -> this.loadBlock( c, workingVec.set( scaledVec ).add( v ) ) );
		}
	}
	
	
	
}
