package cube;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;


public class FreeRotator extends AbstractRotator {

	public FreeRotator(Node cubeNode) {
		super(cubeNode);		
	}

	@Override
	public void rotate(float inputX, float inputY, float inputZ) {
		// Apply rotation to direction vectors
		Quaternion addRot = new Quaternion(inputY * 4, -inputX * 4, 0.0f, 1.0f);
		addRot.multLocal(posVector);
		addRot.multLocal(upVector);
		posVector.normalizeLocal();
		upVector.normalizeLocal();

		// Rotate cube
		cubeNode.lookAt(posVector, upVector);
	}

}
