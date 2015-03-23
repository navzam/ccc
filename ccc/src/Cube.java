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

}
