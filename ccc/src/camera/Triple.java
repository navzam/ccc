package camera;

import cube.Cubie;

public class Triple {
	
    public int x, y, z;
    
    public static Triple[] COLOR_TRIPLES = new Triple[6]; // Order: Red, Green, Orange, Blue, Yellow, White
    
    public static Cubie.Side getColorFromIndex(int index) {
    	if(index == 0)
    		return Cubie.Side.FRONT;
    	if(index == 1)
    		return Cubie.Side.RIGHT;
    	if(index == 2)
    		return Cubie.Side.BACK;
    	if(index == 3)
    		return Cubie.Side.LEFT;
    	if(index == 4)
    		return Cubie.Side.TOP;
    	return Cubie.Side.BOTTOM;
    }

    public Triple(int xC, int yC, int zC){
        x = xC;
        y = yC;
        z = zC;
    }

    public double distance(Triple other){
        // Return difference in hues
    	// TODO: this should be cyclic to handle 0 = 360
    	// angle = 180 - abs(abs(a1 - a2) - 180); 
    	return 180 - Math.abs(Math.abs(other.x - x) - 180);
    }
    
    public static int getMinSaturation() {
    	int minSat = Integer.MAX_VALUE;
    	
    	// Don't consider white reference
    	for(int i = 0; i < 5; ++i) {
    		if(COLOR_TRIPLES[i].y < minSat)
    			minSat = COLOR_TRIPLES[i].y;
    	}
    	
    	return minSat;
    }

    @Override
    public String toString(){
        return "{" + x + ", " + y + ", " + z + "}";
    }
}