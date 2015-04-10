public class AxisVector {
	
	public static enum Axis {
		X_AXIS(0), Y_AXIS(1), Z_AXIS(2);
		
		public final int value;
		
		private Axis(int value) {
			this.value = value;
		}
	}
	
	public AxisVector(Axis startAxis, boolean negDir) {
		final int dir = negDir ? -1 : 1;
		vector[startAxis.value] = dir;
	}
	
	public AxisVector(AxisVector av) {
		System.arraycopy(av.vector, 0, vector, 0, vector.length);
	}
	
	public int getAxisValue(Axis axis) {
		return vector[axis.value];
	}
	
	// Sets this vector to this x av
	public void crossFront(AxisVector av) {
		final int oldX = vector[0];
		final int oldY = vector[1];
		final int oldZ = vector[2];
		
		vector[0] = av.vector[1] * oldZ - av.vector[2] * oldY;
		vector[1] = av.vector[2] * oldX - av.vector[0] * oldZ;
		vector[2] = av.vector[0] * oldY - av.vector[1] * oldX;
	}
	
	public static AxisVector cross(AxisVector a, AxisVector b) {
		AxisVector res = new AxisVector(b);
		res.crossFront(a);
		return res;	
	}
		
	private int[] vector = new int[3];

}
