package util;
import com.jme3.material.Material;

import cube.Cubie;

public final class MaterialManager {
	
	private static Material[] sideMats = new Material[6];
	private static Material defaultMat;
	
	private MaterialManager() {
		throw new AssertionError();
	}
	
	public static Material getMaterial(Cubie.Side side) {
		if(side == null)
			return defaultMat;
		
		return sideMats[side.value];
	}
	
	public static void setMaterial(Cubie.Side side, Material mat) {
		if(side == null)
			defaultMat = mat;
		else
			sideMats[side.value] = mat;
	}

}
