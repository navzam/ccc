import java.util.Random;

import org.kociemba.twophase.Search;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class CubeAppState extends AbstractAppState {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.sApp = (SimpleApplication)app;
		this.cam = sApp.getCamera();
		this.inputManager = sApp.getInputManager();

		// Add mappings and listeners to input manager
		initInput();
		
		// Create cube and populate cube node with cubies
		cube = new Cube();
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					this.cubeNode.attachChild(cube.getCubie(x, y, z).getCenterNode());
		
		sApp.getRootNode().attachChild(this.cubeNode);
	}

	private void initInput() {
		inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
		inputManager.addMapping(MAPPING_ROTATE_LEFT, TRIGGER_ROTATE_LEFT);
		inputManager.addMapping(MAPPING_ROTATE_RIGHT, TRIGGER_ROTATE_RIGHT);
		inputManager.addMapping(MAPPING_ROTATE_UP, TRIGGER_ROTATE_UP);
		inputManager.addMapping(MAPPING_ROTATE_DOWN, TRIGGER_ROTATE_DOWN);

		inputManager.addListener(clickListener, MAPPING_ROTATE);
		inputManager.addListener(moveListener, MAPPING_ROTATE_LEFT, MAPPING_ROTATE_RIGHT, MAPPING_ROTATE_UP, MAPPING_ROTATE_DOWN);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		
		sApp.getRootNode().detachChild(this.cubeNode);
		this.cubeNode.detachAllChildren();
		cube = null;
		
		cleanupInput();
		
		this.inputManager = null;
		this.cam = null;
	}
	
	private void cleanupInput() {
		inputManager.removeListener(clickListener);
		inputManager.removeListener(moveListener);
		
		inputManager.deleteMapping(MAPPING_ROTATE);
		inputManager.deleteMapping(MAPPING_ROTATE_LEFT);
		inputManager.deleteMapping(MAPPING_ROTATE_RIGHT);
		inputManager.deleteMapping(MAPPING_ROTATE_UP);
		inputManager.deleteMapping(MAPPING_ROTATE_DOWN);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	public void update(float tpf) {
		// Nothing to update if the mouse hasn't dragged
		if(dragX == 0.0f && dragY == 0.0f)
			return;

		// If the cube is currently rotating
		if(isCubeRotating) {
			// Apply rotation to direction vectors
			Quaternion addRot = new Quaternion(dragY * 4, -dragX * 4, 0.0f, 1.0f);
			addRot.multLocal(posVector);
			addRot.multLocal(upVector);
			posVector.normalizeLocal();
			upVector.normalizeLocal();

			// Rotate cube
			cubeNode.lookAt(posVector, upVector);

			// Reset movement variables
			dragX = 0.0f;
			dragY = 0.0f;
		}
		// If a face is currently rotating
		else if(isFaceRotating) {
			// If a face has not yet been chosen
			if(!isFaceChosen) {
				// If we are ready to choose a face
				if(Math.abs(dragX) > 0.01f || Math.abs(dragY) > 0.01f) {
					isFaceChosen = true;

					// Determine which face to rotate
					Vector3f cross = new Vector3f();
					chosenNormVector.cross(dragX, dragY, 0.0f, cross);

					final float[] crossAbs = {Math.abs(cross.dot(upVector.cross(posVector))), Math.abs(cross.dot(upVector)), Math.abs(cross.dot(posVector))};
					chosenAxis = 0;
					if(crossAbs[1] > crossAbs[0])
						chosenAxis = 1;
					if(crossAbs[2] > crossAbs[chosenAxis])
						chosenAxis = 2;

					// Populate rotation node
					int[] lows = {0,0,0};
					int[] highs = {2,2,2};
					lows[chosenAxis] = chosenCubiePos[chosenAxis];
					highs[chosenAxis] = chosenCubiePos[chosenAxis];

					rotationNode.setLocalRotation(new Quaternion());
					for(int x = lows[0]; x <= highs[0]; ++x)
						for(int y = lows[1]; y <= highs[1]; ++y)
							for(int z = lows[2]; z <= highs[2]; ++z)
								rotationNode.attachChild(cube.getCubie(x, y, z).getCenterNode());

					// Add rotation node back to scene
					cubeNode.attachChild(rotationNode);
				}
			}
			// A face has been chosen
			else {
				Vector3f cross = new Vector3f();
				chosenNormVector.cross(dragX, dragY, 0.0f, cross);

				final float speedMultiplier = 6.0f;
				if(chosenAxis == 0)
					rotationNode.rotate(-cross.dot(upVector.cross(posVector)) * speedMultiplier, 0.0f, 0.0f);
				else if(chosenAxis == 1)
					rotationNode.rotate(0.0f, -cross.dot(upVector) * speedMultiplier, 0.0f);
				else if(chosenAxis == 2)
					rotationNode.rotate(0.0f, 0.0f, -cross.dot(posVector) * speedMultiplier);	

				dragX = 0.0f;
				dragY = 0.0f;
			}
		}
	}

	private ActionListener clickListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if(name.equals(MAPPING_ROTATE)) {
				// If rotate trigger was pressed
				if(keyPressed) {
					// Calculate collisions with cubies
					Vector2f click2d = inputManager.getCursorPosition();
					Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.0f);
					Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1.0f).subtractLocal(click3d);

					Ray ray = new Ray(click3d, dir);
					CollisionResults results = new CollisionResults();
					cubeNode.collideWith(ray, results);

					// If a cubie was clicked
					if(results.size() != 0) {
						CollisionResult res = results.getClosestCollision();
						chosenNormVector = res.getContactNormal();

						Spatial clickedNode = res.getGeometry().getParent();
						chosenCubiePos[0] = clickedNode.getUserData(Cubie.KEY_X_POS);
						chosenCubiePos[1] = clickedNode.getUserData(Cubie.KEY_Y_POS);
						chosenCubiePos[2] = clickedNode.getUserData(Cubie.KEY_Z_POS);

						isFaceRotating = true;
					}
					else
						isCubeRotating = true;
				}
				// If rotate trigger was released
				else {
					if(isFaceRotating && isFaceChosen) {
						// Calculate direction and number of rotations
						Quaternion q = rotationNode.getLocalRotation();
						Vector3f totalAxis = new Vector3f();
						float totalRot = q.toAngleAxis(totalAxis);

						int numRots = ((int)((totalRot + FastMath.QUARTER_PI) / FastMath.HALF_PI)) % 4;
						boolean cw = totalAxis.get(chosenAxis) < 0.0f;
						if(chosenCubiePos[chosenAxis] == 0)
							cw = !cw;
						if(numRots == 3) {
							numRots = 1;
							cw = !cw;
						}

						// Logically rotate the face
						Cubie.Side rotSide = null;
						if(chosenAxis == 0) {
							if(chosenCubiePos[0] == 0)
								rotSide = Cubie.Side.LEFT;
							else
								rotSide = Cubie.Side.RIGHT;
						}
						else if(chosenAxis == 1) {
							if(chosenCubiePos[1] == 0)
								rotSide = Cubie.Side.BOTTOM;
							else
								rotSide = Cubie.Side.TOP;
						}
						else if(chosenAxis == 2) {
							if(chosenCubiePos[2] == 0)
								rotSide = Cubie.Side.BACK;
							else
								rotSide = Cubie.Side.FRONT;
						}
						for(int i = 0; i < numRots; ++i)
							cube.rotateFace(rotSide, cw);

						// Reset all rotation state variables
						isFaceChosen = false;
						for(int i = 0; i < 3; ++i)
							chosenCubiePos[i] = -1;
						chosenNormVector = null;
						chosenAxis = -1;

						// Attach nodes cubie nodes back to rootNode
						while(rotationNode.getQuantity() > 0)
							cubeNode.attachChild(rotationNode.getChild(0));
						cubeNode.detachChild(rotationNode);
					}

					isFaceRotating = false;
					isCubeRotating = false;
				}
			}
		}
	};

	private AnalogListener moveListener = new AnalogListener() {
		public void onAnalog(String name, float value, float tpf) {
			if(!isCubeRotating && !isFaceRotating)
				return;

			if(name.equals(MAPPING_ROTATE_LEFT))
				dragX -= value;
			else if(name.equals(MAPPING_ROTATE_RIGHT))
				dragX += value;
			else if(name.equals(MAPPING_ROTATE_UP))
				dragY += value;
			else if(name.equals(MAPPING_ROTATE_DOWN))
				dragY -= value;
		}
	};
	
	public void scrambleCube() {
		final Random random = new Random();
		final int numMoves = 100;
		final Cubie.Side[] sides = Cubie.Side.values();
		
		for(int i = 0; i < numMoves; ++i) {
			final int side = random.nextInt(6);
			final int numTurns = random.nextInt(3) + 1;
			
			for(int turn = 0; turn < numTurns; ++turn)
				cube.rotateFace(sides[side], false);
		}
	}
	
	public String solveCube() {
		return Search.solution(cube.toDefinitionString(), 21, 5, false);
	}
	
	public boolean isCubeSolved() {
		return cube.isSolved();
	}
	
	public Node getCubeNode() {
		return cubeNode;
	}
	
	private SimpleApplication sApp;
	private InputManager inputManager;
	private Camera cam;
	private Node cubeNode = new Node();

	private Cube cube;

	private boolean isCubeRotating = false;
	private boolean isFaceRotating = false;
	private boolean isFaceChosen = false;

	private int chosenCubiePos[] = new int[3];
	private Vector3f chosenNormVector;
	private int chosenAxis = -1;

	private Node rotationNode = new Node();

	private Vector3f posVector = new Vector3f(0, 0, 1);
	private Vector3f upVector = new Vector3f(0, 1, 0);

	private float dragX = 0.0f;
	private float dragY = 0.0f;

	private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
	private final static Trigger TRIGGER_ROTATE_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
	private final static Trigger TRIGGER_ROTATE_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
	private final static Trigger TRIGGER_ROTATE_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
	private final static Trigger TRIGGER_ROTATE_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, false);

	private final static String MAPPING_ROTATE = "Rotate Start";
	private final static String MAPPING_ROTATE_LEFT = "Rotate Left";
	private final static String MAPPING_ROTATE_RIGHT = "Rotate Right";
	private final static String MAPPING_ROTATE_UP = "Rotate Up";
	private final static String MAPPING_ROTATE_DOWN = "Rotate Down";

}
