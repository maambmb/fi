package game.block;

import java.util.Map;

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
	
}
