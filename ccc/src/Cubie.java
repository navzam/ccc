import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class Cubie {
	
	public static enum Side {
		FRONT(0), BACK(1), LEFT(2), RIGHT(3), TOP(4), BOTTOM(5);
		
		public final int value;
		
		private Side(int value) {
			this.value = value;
		}
	}
	
	private static final float cubieSpaceWidth = 1.0f;
	
	private static final float cubieWidth = 0.98f;
	private static final float cubieWidthHalf = cubieWidth/2;
	
	public Cubie(int x, int y, int z) {
		colors = new Side[6];
		geoms = new Geometry[6];
		
		geoms[Side.FRONT.value] = new Geometry("front", new Quad(cubieWidth, cubieWidth));
		geoms[Side.FRONT.value].setLocalTranslation(-cubieWidthHalf, -cubieWidthHalf, cubieWidthHalf);
		//geoms[Side.FRONT.value].move(0.01f, 0.01f, 0.0f);
		
		geoms[Side.BACK.value] = new Geometry("back", new Quad(cubieWidth, cubieWidth));
		geoms[Side.BACK.value].setLocalTranslation(cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
		//geoms[Side.BACK.value].move(0.01f, 0.01f, 0.0f);
		geoms[Side.BACK.value].rotate(0.0f, 180 * FastMath.DEG_TO_RAD, 0.0f);
		
		geoms[Side.LEFT.value] = new Geometry("left", new Quad(cubieWidth, cubieWidth));
		geoms[Side.LEFT.value].rotate(0.0f, -90 * FastMath.DEG_TO_RAD, 0.0f);
		geoms[Side.LEFT.value].setLocalTranslation(-cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
		//geoms[Side.LEFT.value].move(0.01f, 0.01f, 0.0f);
		
		geoms[Side.RIGHT.value] = new Geometry("right", new Quad(cubieWidth, cubieWidth));
		geoms[Side.RIGHT.value].rotate(0.0f, 90 * FastMath.DEG_TO_RAD, 0.0f);
		geoms[Side.RIGHT.value].setLocalTranslation(cubieWidthHalf, -cubieWidthHalf, cubieWidthHalf);
		//geoms[Side.RIGHT.value].move(0.01f, 0.01f, 0.0f);
		
		geoms[Side.TOP.value] = new Geometry("top", new Quad(cubieWidth, cubieWidth));
		geoms[Side.TOP.value].setLocalTranslation(-cubieWidthHalf, cubieWidthHalf, cubieWidthHalf);
		//geoms[Side.TOP.value].move(0.01f, 0.01f, 0.0f);
		geoms[Side.TOP.value].rotate(-90 * FastMath.DEG_TO_RAD, 0.0f, 0.0f);
		
		geoms[Side.BOTTOM.value] = new Geometry("bottom", new Quad(cubieWidth, cubieWidth));
		geoms[Side.BOTTOM.value].rotate(90 * FastMath.DEG_TO_RAD, 0.0f, 0.0f);
		geoms[Side.BOTTOM.value].setLocalTranslation(-cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
		//geoms[Side.BOTTOM.value].move(0.01f, 0.01f, 0.0f);
		
		for(int i = 0; i < 6; ++i)
			geoms[i].setMaterial(MaterialManager.getMaterial(null));
		
		centerNode = new Node();
		centerNode.setLocalTranslation(cubieSpaceWidth * (x - 1), cubieSpaceWidth * (y - 1), cubieSpaceWidth * (z - 1));
		for(int i = 0; i < 6; ++i)
			centerNode.attachChild(geoms[i]);
	}
	
	public void setColor(Side side, Side color) {
		colors[side.value] = color;
		geoms[side.value].setMaterial(MaterialManager.getMaterial(color));
	}
	
	public Side getColor(Side side) {
		return colors[side.value];
	}
	
	public Node getCenterNode() {
		return centerNode;
	}
	
	private Side colors[];
	private Node centerNode;
	private Geometry geoms[];

}
