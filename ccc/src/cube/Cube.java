package cube;
import cube.FaceTurn.Direction;
import util.AxisVector;

public class Cube {
	
	private Cubie cubies[][][];
	
	public Cube() {
		// Initialize the cubies with the proper colors
		cubies = new Cubie[3][3][3];
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					cubies[x][y][z] = new Cubie(x, y, z);
		
		for(int y = 0; y < 3; ++y) {
			for(int z = 0; z < 3; ++z) {
				cubies[0][y][z].setColor(Cubie.Side.LEFT, Cubie.Side.LEFT);
				cubies[2][y][z].setColor(Cubie.Side.RIGHT, Cubie.Side.RIGHT);
			}
		}
		
		for(int x = 0; x < 3; ++x) {
			for(int z = 0; z < 3; ++z) {
				cubies[x][0][z].setColor(Cubie.Side.BOTTOM, Cubie.Side.BOTTOM);
				cubies[x][2][z].setColor(Cubie.Side.TOP, Cubie.Side.TOP);
			}
		}
		
		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 3; ++y) {
				cubies[x][y][0].setColor(Cubie.Side.BACK, Cubie.Side.BACK);
				cubies[x][y][2].setColor(Cubie.Side.FRONT, Cubie.Side.FRONT);
			}
		}
	}
	
	public void rotateFace(FaceTurn faceTurn) {
		final Cubie.Side side = faceTurn.getFace();
		final int numTurns = faceTurn.getDirection() == Direction.DOUBLE ? 2 : 1;
		final boolean cw = faceTurn.getDirection() == Direction.CLOCKWISE ? true : false;
		
		if(side == Cubie.Side.LEFT || side == Cubie.Side.RIGHT) {
			for(int turn = 0; turn < numTurns; ++turn)
				this.rotateFaceX(side, cw);
		}
		else if(side == Cubie.Side.BOTTOM || side == Cubie.Side.TOP) {
			for(int turn = 0; turn < numTurns; ++turn)
				this.rotateFaceY(side, cw);
		}
		else if(side == Cubie.Side.BACK || side == Cubie.Side.FRONT) {
			for(int turn = 0; turn < numTurns; ++turn)
				this.rotateFaceZ(side, cw);
		}
	}
	
	private void rotateFaceX(Cubie.Side side, boolean cw) {
		if(side != Cubie.Side.LEFT && side != Cubie.Side.RIGHT) {
			System.err.println("rotateFaceX called incorrectly!");
			return;
		}
		
		// Rotate cubies array (specific for 3x3x3)
		int fixedX = side == Cubie.Side.LEFT ? 0 : 2;
		boolean cubieRotCW;
		if((side == Cubie.Side.LEFT && cw) || (side == Cubie.Side.RIGHT && !cw)) {
			Cubie temp = cubies[fixedX][0][0];
			cubies[fixedX][0][0] = cubies[fixedX][0][2];
			cubies[fixedX][0][2] = cubies[fixedX][2][2];
			cubies[fixedX][2][2] = cubies[fixedX][2][0];
			cubies[fixedX][2][0] = temp;
			
			temp = cubies[fixedX][1][0];
			cubies[fixedX][1][0] = cubies[fixedX][0][1];
			cubies[fixedX][0][1] = cubies[fixedX][1][2];
			cubies[fixedX][1][2] = cubies[fixedX][2][1];
			cubies[fixedX][2][1] = temp;
			
			cubieRotCW = false;
		}
		else {
			Cubie temp = cubies[fixedX][0][0];
			cubies[fixedX][0][0] = cubies[fixedX][2][0];
			cubies[fixedX][2][0] = cubies[fixedX][2][2];
			cubies[fixedX][2][2] = cubies[fixedX][0][2];
			cubies[fixedX][0][2] = temp;
			
			temp = cubies[fixedX][1][0];
			cubies[fixedX][1][0] = cubies[fixedX][2][1];
			cubies[fixedX][2][1] = cubies[fixedX][1][2];
			cubies[fixedX][1][2] = cubies[fixedX][0][1];
			cubies[fixedX][0][1] = temp;
			
			cubieRotCW = true;
		}
		
		// Rotate individual cubies
		for(int y = 0; y < 3; ++y)
			for(int z = 0; z < 3; ++z) {
				cubies[fixedX][y][z].rotate(AxisVector.Axis.X_AXIS, cubieRotCW);
				cubies[fixedX][y][z].setPos(fixedX, y, z);
			}
	}
	
	private void rotateFaceY(Cubie.Side side, boolean cw) {
		if(side != Cubie.Side.TOP && side != Cubie.Side.BOTTOM) {
			System.err.println("rotateFaceY called incorrectly!");
			return;
		}
		
		// Rotate cubies array (specific for 3x3x3)
		int fixedY = side == Cubie.Side.BOTTOM ? 0 : 2;
		boolean cubieRotCW;
		if((side == Cubie.Side.BOTTOM && cw) || (side == Cubie.Side.TOP && !cw)) {
			Cubie temp = cubies[0][fixedY][0];
			cubies[0][fixedY][0] = cubies[2][fixedY][0];
			cubies[2][fixedY][0] = cubies[2][fixedY][2];
			cubies[2][fixedY][2] = cubies[0][fixedY][2];
			cubies[0][fixedY][2] = temp;
			
			temp = cubies[0][fixedY][1];
			cubies[0][fixedY][1] = cubies[1][fixedY][0];
			cubies[1][fixedY][0] = cubies[2][fixedY][1];
			cubies[2][fixedY][1] = cubies[1][fixedY][2];
			cubies[1][fixedY][2] = temp;
			
			cubieRotCW = false;
		}
		else {
			Cubie temp = cubies[0][fixedY][0];
			cubies[0][fixedY][0] = cubies[0][fixedY][2];
			cubies[0][fixedY][2] = cubies[2][fixedY][2];
			cubies[2][fixedY][2] = cubies[2][fixedY][0];
			cubies[2][fixedY][0] = temp;
			
			temp = cubies[0][fixedY][1];
			cubies[0][fixedY][1] = cubies[1][fixedY][2];
			cubies[1][fixedY][2] = cubies[2][fixedY][1];
			cubies[2][fixedY][1] = cubies[1][fixedY][0];
			cubies[1][fixedY][0] = temp;
			
			cubieRotCW = true;
		}
		
		// Rotate individual cubies
		for(int x = 0; x < 3; ++x)
			for(int z = 0; z < 3; ++z) {
				cubies[x][fixedY][z].rotate(AxisVector.Axis.Y_AXIS, cubieRotCW);
				cubies[x][fixedY][z].setPos(x, fixedY, z);
			}
	}

	private void rotateFaceZ(Cubie.Side side, boolean cw) {
		if(side != Cubie.Side.BACK && side != Cubie.Side.FRONT) {
			System.err.println("rotateFaceZ called incorrectly!");
			return;
		}
		
		// Rotate cubies array (specific for 3x3x3)
		int fixedZ = side == Cubie.Side.BACK ? 0 : 2;
		boolean cubieRotCW;
		if((side == Cubie.Side.BACK && cw) || (side == Cubie.Side.FRONT && !cw)) {
			Cubie temp = cubies[0][0][fixedZ];
			cubies[0][0][fixedZ] = cubies[0][2][fixedZ];
			cubies[0][2][fixedZ] = cubies[2][2][fixedZ];
			cubies[2][2][fixedZ] = cubies[2][0][fixedZ];
			cubies[2][0][fixedZ] = temp;
			
			temp = cubies[0][1][fixedZ];
			cubies[0][1][fixedZ] = cubies[1][2][fixedZ];
			cubies[1][2][fixedZ] = cubies[2][1][fixedZ];
			cubies[2][1][fixedZ] = cubies[1][0][fixedZ];
			cubies[1][0][fixedZ] = temp;
			
			cubieRotCW = false;
		}
		else {
			Cubie temp = cubies[0][0][fixedZ];
			cubies[0][0][fixedZ] = cubies[2][0][fixedZ];
			cubies[2][0][fixedZ] = cubies[2][2][fixedZ];
			cubies[2][2][fixedZ] = cubies[0][2][fixedZ];
			cubies[0][2][fixedZ] = temp;
			
			temp = cubies[0][1][fixedZ];
			cubies[0][1][fixedZ] = cubies[1][0][fixedZ];
			cubies[1][0][fixedZ] = cubies[2][1][fixedZ];
			cubies[2][1][fixedZ] = cubies[1][2][fixedZ];
			cubies[1][2][fixedZ] = temp;
			
			cubieRotCW = true;
		}
		
		// Rotate individual cubies
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y) {
				cubies[x][y][fixedZ].rotate(AxisVector.Axis.Z_AXIS, cubieRotCW);
				cubies[x][y][fixedZ].setPos(x, y, fixedZ);
			}
	}
	
	public boolean isSolved() {
		for(int c1 = 0; c1 < 3; ++c1) {
			for(int c2 = 0; c2 < 3; ++c2) {
				if(cubies[0][c1][c2].getColor(Cubie.Side.LEFT) != cubies[0][1][1].getColor(Cubie.Side.LEFT)
						|| cubies[2][c1][c2].getColor(Cubie.Side.RIGHT) != cubies[2][1][1].getColor(Cubie.Side.RIGHT)
						|| cubies[c1][0][c2].getColor(Cubie.Side.BOTTOM) != cubies[1][0][1].getColor(Cubie.Side.BOTTOM)
						|| cubies[c1][2][c2].getColor(Cubie.Side.TOP) != cubies[1][2][1].getColor(Cubie.Side.TOP)
						|| cubies[c1][c2][0].getColor(Cubie.Side.BACK) != cubies[1][1][0].getColor(Cubie.Side.BACK)
						|| cubies[c1][c2][2].getColor(Cubie.Side.FRONT) != cubies[1][1][2].getColor(Cubie.Side.FRONT))
					return false;
			}
		}
		
		return true;
	}
	
	public Cubie getCubie(int x, int y, int z) {
		if(x < 0 || x > 2 || y < 0 || y > 2 || z < 0 || z > 2)
		{
			System.err.println("Invalid cubie coordinate!");
			return null;
		}
		
		return cubies[x][y][z];
	}
	
	public String toDefinitionString() {
		StringBuilder sb = new StringBuilder();
				
		// Top face
		for(int z = 0; z < 3; ++z)
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][2][z].getColor(Cubie.Side.TOP));
		
		// Right face
		for(int y = 2; y >= 0; --y)
			for(int z = 2; z >= 0; --z)
				sb.append(cubies[2][y][z].getColor(Cubie.Side.RIGHT));
			
		// Front face
		for(int y = 2; y >= 0; --y)
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][y][2].getColor(Cubie.Side.FRONT));
		
		// Bottom face
		for(int z = 2; z >= 0; --z)
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][0][z].getColor(Cubie.Side.BOTTOM));
		
		// Left face
		for(int y = 2; y >= 0; --y)
			for(int z = 0; z < 3; ++z)
				sb.append(cubies[0][y][z].getColor(Cubie.Side.LEFT));
		
		// Back face
		for(int y = 2; y >= 0; --y)			
			for(int x = 2; x >= 0; --x)
				sb.append(cubies[x][y][0].getColor(Cubie.Side.BACK));			
		
		return sb.toString();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		final String sep = System.getProperty("line.separator");
		
		// Top face
		for(int z = 0; z < 3; ++z) {
			sb.append("       ");
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][2][z].getColor(Cubie.Side.TOP) + " ");
			sb.append(sep);
		}
		sb.append(sep);
		
		for(int y = 2; y >= 0; --y) {
			// Left face
			for(int z = 0; z < 3; ++z)
				sb.append(cubies[0][y][z].getColor(Cubie.Side.LEFT) + " ");
			sb.append(" ");
			
			// Front face
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][y][2].getColor(Cubie.Side.FRONT) + " ");
			sb.append(" ");
			
			// Right face
			for(int z = 2; z >= 0; --z)
				sb.append(cubies[2][y][z].getColor(Cubie.Side.RIGHT) + " ");
			sb.append(" ");
			
			// Back face
			for(int x = 2; x >= 0; --x)
				sb.append(cubies[x][y][0].getColor(Cubie.Side.BACK) + " ");
			
			sb.append(sep);
		}
		sb.append(sep);
		
		// Bottom face
		for(int z = 2; z >= 0; --z) {
			sb.append("       ");
			for(int x = 0; x < 3; ++x)
				sb.append(cubies[x][0][z].getColor(Cubie.Side.BOTTOM) + " ");
			sb.append(sep);
		}
		
		return sb.toString();
	}

}
