package cube;

import java.util.ArrayList;

public class FaceTurn {
	
	public static enum Direction {
		CLOCKWISE, COUNTERCLOCKWISE, DOUBLE
	}
	
	public static ArrayList<FaceTurn> getFaceTurns(String turnStr) {
		final String[] turnStrs = turnStr.split("\\s+");
		final Cubie.Side[] allSides = Cubie.Side.values();
		ArrayList<FaceTurn> turns = new ArrayList<FaceTurn>();
		
		for(String str : turnStrs) {
			if(str.isEmpty())
				continue;
			
			Direction dir = Direction.DOUBLE;
			if(str.length() != 2)
				dir = Direction.CLOCKWISE;
			else if(str.charAt(1) == '\'')
				dir = Direction.COUNTERCLOCKWISE;
			
			Cubie.Side side = null;
			final char faceChar = str.charAt(0);
			for(Cubie.Side testSide : allSides) {
				if(faceChar == testSide.name) {
					side = testSide;
					break;
				}
			}
			
			turns.add(new FaceTurn(side, dir));
		}
		
		return turns;
	}
	
	public FaceTurn(Cubie.Side face, Direction direction) {
		this.face = face;
		this.direction = direction;
	}
	
	public FaceTurn(Cubie.Side face, boolean cw) {
		this.face = face;
		this.direction = cw ? Direction.CLOCKWISE : Direction.COUNTERCLOCKWISE;
	}
	
	public void reverse() {
		if(direction == Direction.CLOCKWISE)
			direction = Direction.COUNTERCLOCKWISE;
		else if(direction == Direction.COUNTERCLOCKWISE);
			direction = Direction.CLOCKWISE;
	}
	
	public Cubie.Side getFace() {
		return face;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(face.name);
		if(direction == Direction.COUNTERCLOCKWISE)
			sb.append('\'');
		else if(direction == Direction.DOUBLE)
			sb.append('2');
		
		return sb.toString();
	}
	
	private Cubie.Side face;
	private Direction direction;

}
