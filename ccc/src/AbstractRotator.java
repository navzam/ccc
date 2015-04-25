import com.jme3.math.Vector3f;
import com.jme3.scene.Node;


public abstract class AbstractRotator {
	
	public static enum CubeRotationType {
		FREE, TURNTABLE
	}
	
	public final Vector3f getPosVector() {
		return posVector.clone();
	}
	
	public Vector3f getUpVector() {
		return upVector.clone();
	}
	
	public AbstractRotator(Node cubeNode) {
		this.cubeNode = cubeNode;
	}
	
	abstract void rotate(float inputX, float inputY, float inputZ);
	
	protected Vector3f posVector = new Vector3f(0, 0, 1);
	protected Vector3f upVector = new Vector3f(0, 1, 0);
	protected final Node cubeNode;

}
