import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class Cubie {
	
	public static enum Side {
		FRONT(0, 'F'), BACK(1, 'B'), LEFT(2, 'L'), RIGHT(3, 'R'), TOP(4, 'T'), BOTTOM(5, 'D');
		
		public final int value;
		public final char name;
		
		private Side(int value, char name) {
			this.value = value;
			this.name = name;
		}
		
		public String toString() {
			return String.valueOf(name);
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
		
		geoms[Side.BACK.value] = new Geometry("back", new Quad(cubieWidth, cubieWidth));
		geoms[Side.BACK.value].setLocalTranslation(cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
		geoms[Side.BACK.value].rotate(0.0f, 180 * FastMath.DEG_TO_RAD, 0.0f);
		
		geoms[Side.LEFT.value] = new Geometry("left", new Quad(cubieWidth, cubieWidth));
		geoms[Side.LEFT.value].rotate(0.0f, -90 * FastMath.DEG_TO_RAD, 0.0f);
		geoms[Side.LEFT.value].setLocalTranslation(-cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
		
		geoms[Side.RIGHT.value] = new Geometry("right", new Quad(cubieWidth, cubieWidth));
		geoms[Side.RIGHT.value].rotate(0.0f, 90 * FastMath.DEG_TO_RAD, 0.0f);
		geoms[Side.RIGHT.value].setLocalTranslation(cubieWidthHalf, -cubieWidthHalf, cubieWidthHalf);
		
		geoms[Side.TOP.value] = new Geometry("top", new Quad(cubieWidth, cubieWidth));
		geoms[Side.TOP.value].setLocalTranslation(-cubieWidthHalf, cubieWidthHalf, cubieWidthHalf);
		geoms[Side.TOP.value].rotate(-90 * FastMath.DEG_TO_RAD, 0.0f, 0.0f);
		
		geoms[Side.BOTTOM.value] = new Geometry("bottom", new Quad(cubieWidth, cubieWidth));
		geoms[Side.BOTTOM.value].rotate(90 * FastMath.DEG_TO_RAD, 0.0f, 0.0f);
		geoms[Side.BOTTOM.value].setLocalTranslation(-cubieWidthHalf, -cubieWidthHalf, -cubieWidthHalf);
				
		for(int i = 0; i < 6; ++i)
			geoms[i].setMaterial(MaterialManager.getMaterial(null));
		
		centerNode = new Node();
		centerNode.setLocalTranslation(cubieSpaceWidth * (x - 1), cubieSpaceWidth * (y - 1), cubieSpaceWidth * (z - 1));
		for(int i = 0; i < 6; ++i)
			centerNode.attachChild(geoms[i]);
		
		this.setPos(x, y, z);
	}
	
	public void rotate(int axis, boolean cw) {
		final float rotAmt = FastMath.HALF_PI;
		
		// Calculate rotation vector based on state vectors
		int[] rotVector = {0, 0, 0};
		if(upVector[axis] != 0)
			rotVector[1] = upVector[axis];
		else if(outVector[axis] != 0)
			rotVector[2] = outVector[axis];
		else {
			int[] sideVector = this.crossVectors(upVector, outVector);
			if(sideVector[axis] != 0)
				rotVector[0] = sideVector[axis];
		}
		
		// Logical rotation (colors)
		int[] rotColorArr;
		if(axis == 0)
			rotColorArr = new int[] {Side.FRONT.value, Side.TOP.value, Side.BACK.value, Side.BOTTOM.value};
		else if(axis == 1)
			rotColorArr = new int[] {Side.FRONT.value, Side.LEFT.value, Side.BACK.value, Side.RIGHT.value};
		else
			rotColorArr = new int[] {Side.TOP.value, Side.RIGHT.value, Side.BOTTOM.value, Side.LEFT.value};
		if(cw) {
			int temp = rotColorArr[1];
			rotColorArr[1] = rotColorArr[3];
			rotColorArr[3] = temp;
		}
		this.rotateFromArray(rotColorArr);
		
		// Physical rotation (center node)
		final int multiplier = cw ? -1 : 1;
		centerNode.rotate(rotAmt * rotVector[0] * multiplier, rotAmt * rotVector[1] * multiplier, rotAmt * rotVector[2] * multiplier);
		
		// Update state vectors as needed
		int[] stateCrosser = {0, 0, 0};
		stateCrosser[axis] = multiplier;
		if(outVector[axis] == 0)
			outVector = this.crossVectors(stateCrosser, outVector);
		if(upVector[axis] == 0)
			upVector = this.crossVectors(stateCrosser, upVector);
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
	
	public void setPos(int x, int y, int z) {
		centerNode.setUserData("x_pos", x);
		centerNode.setUserData("y_pos", y);
		centerNode.setUserData("z_pos", z);
		centerNode.setLocalTranslation(x - 1, y - 1, z - 1);
	}

	private void rotateFromArray(int[] arr) {
		if(arr.length != 4) {
			System.err.println("rotateFromArray called incorrectly!");
			return;
		}
		
		Side temp = colors[arr[0]];
		for(int i = 0; i < 3; ++i)
			colors[arr[i]] = colors[arr[i+1]];
		colors[arr[3]] = temp;
	}
	
	private int[] crossVectors(int[] u, int[] v) {
		return new int[] {u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0]};
	}
	
	private Side colors[];
	private Node centerNode;
	private Geometry geoms[];
	private int outVector[] = {0, 0, 1};
	private int upVector[] = {0, 1, 0};

}
