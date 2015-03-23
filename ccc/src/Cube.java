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
				cubies[0][y][z].setColor(Cubie.Side.LEFT, Color.ColorName.BLUE);
				cubies[2][y][z].setColor(Cubie.Side.RIGHT, Color.ColorName.GREEN);
			}
		}
		
		for(int x = 0; x < 3; ++x) {
			for(int z = 0; z < 3; ++z) {
				cubies[x][0][z].setColor(Cubie.Side.BOTTOM, Color.ColorName.WHITE);
				cubies[x][2][z].setColor(Cubie.Side.TOP, Color.ColorName.YELLOW);
			}
		}
		
		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 3; ++y) {
				cubies[x][y][0].setColor(Cubie.Side.BACK, Color.ColorName.ORANGE);
				cubies[x][y][2].setColor(Cubie.Side.FRONT, Color.ColorName.RED);
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
