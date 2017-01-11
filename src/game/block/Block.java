package game.block;

import util.Tuple;

public enum Block {

	Gravel(
			Tuple.create( 0, 0, 0),
			Tuple.create( 0,0 ),
			Type.OpaqueBlock
	),
	Glass(
			Tuple.create( 0, 0, 0),
			Tuple.create( 1,0 ),
			Type.TransparentBlock
	);
	
	public enum Type {
		// a block that prevents the propagation of light
		OpaqueBlock,
		// a block that allows the propagation of light
		TransparentBlock,
		// a block created by 2 textured quads forming a cross (X) pattern (assumed to be transparent also)
		CrossBlock
	}

	public Tuple.Ternary<Integer, Integer, Integer> luminescence;
	public Tuple.Binary<Integer,Integer> texCoords;
	public Type blockType;

	private Block(
			Tuple.Ternary<Integer, Integer, Integer> illumination,
			Tuple.Binary<Integer,Integer> texCoords,
			Type blockType
	) {
		this.luminescence = illumination;
		this.texCoords = texCoords;
		this.blockType = blockType;
	}
	
}
