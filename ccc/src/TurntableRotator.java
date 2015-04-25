import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;


public class TurntableRotator extends AbstractRotator {

	public TurntableRotator(Node cubeNode) {
		super(cubeNode);
	}

	@Override
	public void rotate(float inputX, float inputY, float inputZ) {
		// Rotate cube according to new target rotation
		targetRotation.addLocal(inputX * 4, inputY * 4);
		targetRotation.y = Math.min(Math.max(targetRotation.y, -FastMath.HALF_PI), FastMath.HALF_PI);
		cubeNode.setLocalRotation(Quaternion.IDENTITY);
		cubeNode.rotate(targetRotation.y, 0.0f, 0.0f);
		cubeNode.rotate(0.0f, -targetRotation.x, 0.0f);
		
		// Apply rotation to direction vectors
		Quaternion localRot = cubeNode.getLocalRotation();
		localRot.multLocal(posVector.set(0.0f, 0.0f, 1.0f));
		localRot.multLocal(upVector.set(0.0f, 1.0f, 0.0f));
		posVector.normalizeLocal();
		upVector.normalizeLocal();
	}
	
	private Vector2f targetRotation = new Vector2f(0.0f, 0.0f);

}
