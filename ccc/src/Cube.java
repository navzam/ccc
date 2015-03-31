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
	
	public Cubie getCubie(int x, int y, int z) {
		if(x < 0 || x > 2 || y < 0 || y > 2 || z < 0 || z > 2)
		{
			System.err.println("Invalid cubie coordinate!");
			return null;
		}
		
		return cubies[x][y][z];
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
