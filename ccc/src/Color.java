import com.jme3.math.ColorRGBA;

public class Color {
	
	public static enum ColorName {
		RED, ORANGE, BLUE, GREEN, YELLOW, WHITE, DEFAULT
	}
	
	private ColorName name;
	private ColorRGBA rgba;
	
	public Color() {
		this(ColorName.DEFAULT);
	}
	
	public Color(ColorName name) {
		rgba = new ColorRGBA();
		setColor(name);
	}
	
	public void setColor(ColorName name)
	{
		this.name = name;
		switch(name) {
		case RED:
			rgba.set(0.77f, 0.12f, 0.23f, 1.0f); // (196, 30, 58)
			break;
		case ORANGE:
			rgba.set(1.0f, 0.35f, 0.0f, 1.0f); // (255, 88, 0)
			break;
		case BLUE:
			rgba.set(0.0f, 0.32f, 0.73f, 1.0f); // (0, 81, 186)
			break;
		case GREEN:
			rgba.set(0.0f, 0.62f, 0.38f, 1.0f); // (0, 158, 96)
			break;
		case YELLOW:
			rgba.set(1.0f, 0.84f, 0.0f, 1.0f); // (255, 213, 0)
			break;
		case WHITE:
			rgba.set(1.0f, 1.0f, 1.0f, 1.0f); // (255, 255, 255)
			break;
		case DEFAULT:
			rgba.set(0.0f, 0.0f, 0.0f, 1.0f); // (0, 0, 0)
			break;
		default:
			System.err.println("Unhandled color case!");
			break;
		}
	}
	
	public ColorName getName() {
		return name;
	}
	
	public ColorRGBA getRGBA() {
		return rgba;
	}

}
