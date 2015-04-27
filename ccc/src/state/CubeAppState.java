package state;
import java.util.Random;

import org.kociemba.twophase.Search;

import util.CCCConstants;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import cube.AbstractRotator;
import cube.Cube;
import cube.Cubie;
import cube.Cubie.Side;
import cube.FaceTurn;
import cube.FaceTurn.Direction;
import cube.FreeRotator;
import cube.TurntableRotator;

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
		
		// Set cube rotator
		AbstractRotator.CubeRotationType crType = (AbstractRotator.CubeRotationType) sApp.getContext().getSettings().get(CCCConstants.Settings.CUBE_ROTATION_TYPE);
		if(crType == AbstractRotator.CubeRotationType.FREE)
			cubeRotator = new FreeRotator(cubeNode);
		else
			cubeRotator = new TurntableRotator(cubeNode);
	}

	private void initInput() {
		inputManager.addMapping(CCCConstants.Input.MAPPING_ROTATE, CCCConstants.Input.TRIGGER_ROTATE);
		inputManager.addMapping(CCCConstants.Input.MAPPING_ROTATE_LEFT, CCCConstants.Input.TRIGGER_ROTATE_LEFT);
		inputManager.addMapping(CCCConstants.Input.MAPPING_ROTATE_RIGHT, CCCConstants.Input.TRIGGER_ROTATE_RIGHT);
		inputManager.addMapping(CCCConstants.Input.MAPPING_ROTATE_UP, CCCConstants.Input.TRIGGER_ROTATE_UP);
		inputManager.addMapping(CCCConstants.Input.MAPPING_ROTATE_DOWN, CCCConstants.Input.TRIGGER_ROTATE_DOWN);
		
		inputManager.addMapping(CCCConstants.Input.MAPPING_DIRECT_LEFT, CCCConstants.Input.TRIGGER_DIRECT_LEFT);
		inputManager.addMapping(CCCConstants.Input.MAPPING_DIRECT_RIGHT, CCCConstants.Input.TRIGGER_DIRECT_RIGHT);
		inputManager.addMapping(CCCConstants.Input.MAPPING_DIRECT_UP, CCCConstants.Input.TRIGGER_DIRECT_UP);
		inputManager.addMapping(CCCConstants.Input.MAPPING_DIRECT_DOWN, CCCConstants.Input.TRIGGER_DIRECT_DOWN);

		inputManager.addListener(clickListener,
				CCCConstants.Input.MAPPING_ROTATE,
				CCCConstants.Input.MAPPING_DIRECT_LEFT,
				CCCConstants.Input.MAPPING_DIRECT_RIGHT,
				CCCConstants.Input.MAPPING_DIRECT_UP,
				CCCConstants.Input.MAPPING_DIRECT_DOWN);
		inputManager.addListener(moveListener,
				CCCConstants.Input.MAPPING_ROTATE_LEFT,
				CCCConstants.Input.MAPPING_ROTATE_RIGHT,
				CCCConstants.Input.MAPPING_ROTATE_UP,
				CCCConstants.Input.MAPPING_ROTATE_DOWN);
		
		frEnabled = true;
	}
	
	public void enableFaceRotation(boolean enable) {
		frEnabled = enable;
	}
	
	public boolean isFaceRotationEnabled() {
		return frEnabled;
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
		
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_ROTATE);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_ROTATE_LEFT);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_ROTATE_RIGHT);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_ROTATE_UP);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_ROTATE_DOWN);
		
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_DIRECT_LEFT);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_DIRECT_RIGHT);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_DIRECT_UP);
		inputManager.deleteMapping(CCCConstants.Input.MAPPING_DIRECT_DOWN);
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
			// Rotate the cube according to drags
			cubeRotator.rotate(dragX, dragY, 0.0f);

			// Reset drag variables
			dragX = 0.0f;
			dragY = 0.0f;
		}
		// If a face is currently rotating
		else if(isFaceRotating) {
			// If a face has not been chosen and we are ready to choose one
			if(!isFaceChosen && (Math.abs(dragX) > 0.01f || Math.abs(dragY) > 0.01f)) {
				isFaceChosen = true;

				// Determine which face to rotate
				final Vector3f cross = chosenNormVector.cross(new Vector3f(dragX, dragY, 0.0f));
				chosenAxis = getChosenRotationAxis(cross, cubeRotator.getUpVector(), cubeRotator.getPosVector());
				
				// Move chosen cubies to rotationNode
				cubeNodeToRotationNode();
			}
			// A face has been chosen
			else {
				final Vector3f cross = chosenNormVector.cross(new Vector3f(dragX, dragY, 0.0f));
				final Vector3f upVector = cubeRotator.getUpVector();
				final Vector3f posVector = cubeRotator.getPosVector();
				
				// Physically rotate the currently rotating face
				final float speedMultiplier = 6.0f;
				if(chosenAxis == 0)
					rotationNode.rotate(-cross.dot(upVector.cross(posVector)) * speedMultiplier, 0.0f, 0.0f);
				else if(chosenAxis == 1)
					rotationNode.rotate(0.0f, -cross.dot(upVector) * speedMultiplier, 0.0f);
				else if(chosenAxis == 2)
					rotationNode.rotate(0.0f, 0.0f, -cross.dot(posVector) * speedMultiplier);	
				
				// Reset mouse drags
				dragX = 0.0f;
				dragY = 0.0f;
			}
		}
	}
	
	private AnalogListener moveListener = new AnalogListener() {
		public void onAnalog(String name, float value, float tpf) {
			if(!isCubeRotating && !isFaceRotating)
				return;

			if(name.equals(CCCConstants.Input.MAPPING_ROTATE_LEFT))
				dragX -= value;
			else if(name.equals(CCCConstants.Input.MAPPING_ROTATE_RIGHT))
				dragX += value;
			else if(name.equals(CCCConstants.Input.MAPPING_ROTATE_UP))
				dragY += value;
			else if(name.equals(CCCConstants.Input.MAPPING_ROTATE_DOWN))
				dragY -= value;
		}
	};

	private ActionListener clickListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if(name.equals(CCCConstants.Input.MAPPING_ROTATE)) {
				// If rotate trigger was pressed
				if(keyPressed) {
					// If face rotation is disabled or a cubie wasn't clicked, rotate whole cube
					if(!frEnabled || !chooseCubieFromMouse())
						isCubeRotating = true;
				}
				// If rotate trigger was released
				else {
					if(isFaceRotating && isFaceChosen) {
						// Calculate total number of rotations
						final Quaternion q = rotationNode.getLocalRotation();
						final Vector3f totalAxis = new Vector3f();
						final float totalRot = q.toAngleAxis(totalAxis);
						int numRots = ((int)((totalRot + FastMath.QUARTER_PI) / FastMath.HALF_PI)) % 4;
						
						// If face rotated at least once
						if(numRots != 0) {
							// Calculate rotation direction
							boolean cw = totalAxis.get(chosenAxis) < 0.0f;
							if(chosenCubiePos[chosenAxis] == 0)
								cw = !cw;
							
							// Reduce 3 rotations to 1 rotation
							if(numRots == 3) {
								numRots = 1;
								cw = !cw;
							}
	
							// Logically rotate the face
							final Cubie.Side rotSide = getChosenRotationSide();
							if(numRots == 2)
								cube.rotateFace(new FaceTurn(rotSide, Direction.DOUBLE));
							else
								cube.rotateFace(new FaceTurn(rotSide, cw));
						}

						// Reset all rotation state variables
						resetFaceRotationVars();

						// Attach cubie nodes back to rootNode
						rotationNodeToCubeNode();
					}

					isCubeRotating = false;
				}
			}
			else if(!keyPressed && (name.equals(CCCConstants.Input.MAPPING_DIRECT_LEFT)
					|| name.equals(CCCConstants.Input.MAPPING_DIRECT_RIGHT)
					|| name.equals(CCCConstants.Input.MAPPING_DIRECT_UP)
					|| name.equals(CCCConstants.Input.MAPPING_DIRECT_DOWN))) {
				// Determine x-y direction based on which arrow key
				int dirX = 0;
				int dirY = 0;
				if(name.equals(CCCConstants.Input.MAPPING_DIRECT_LEFT))
					dirX = -1;
				else if(name.equals(CCCConstants.Input.MAPPING_DIRECT_RIGHT))
					dirX = 1;
				else if(name.equals(CCCConstants.Input.MAPPING_DIRECT_UP))
					dirY = 1;
				else
					dirY = -1;
				
				// If there was a cubie under the mouse
				if(chooseCubieFromMouse()) {
					// Figure out which axis to rotate on
					final Vector3f cross = chosenNormVector.cross(new Vector3f(dirX, dirY, 0.0f));
					final Vector3f upVector = cubeRotator.getUpVector();
					final Vector3f posVector = cubeRotator.getPosVector();
					chosenAxis = getChosenRotationAxis(cross, upVector, posVector);
					
					// Move chosen cubies to rotationNode
					cubeNodeToRotationNode();
					
					// Calculate rotation direction
					boolean cw = false;
					if(chosenAxis == 0)
						cw = cross.dot(upVector.cross(posVector)) < 0.0f;
					else if(chosenAxis == 1)
						cw = cross.dot(upVector) < 0.0f;
					else if(chosenAxis == 2)
						cw = cross.dot(posVector) < 0.0f;
					if(chosenCubiePos[chosenAxis] == 0)
						cw = !cw;
	
					// Logically rotate the face
					final Side rotSide = getChosenRotationSide();
					cube.rotateFace(new FaceTurn(rotSide, cw));
	
					// Reset all rotation state variables
					resetFaceRotationVars();
	
					// Attach cubie nodes back to rootNode
					rotationNodeToCubeNode();
				}
			}
		}
	};
	
	// Returns collision result for closest cubie under mouse
	private CollisionResult closestMouseCollision() {
		Vector2f click2d = inputManager.getCursorPosition();
		Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.0f);
		Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1.0f).subtractLocal(click3d);

		Ray ray = new Ray(click3d, dir);
		CollisionResults results = new CollisionResults();
		cubeNode.collideWith(ray, results);
		
		if(results.size() == 0)
			return null;
		return results.getClosestCollision();
	}
	
	// Chooses closest cubie under mouse
	private boolean chooseCubieFromMouse() {
		final CollisionResult res = closestMouseCollision();
		if(res == null)
			return false;
		
		chosenNormVector = res.getContactNormal();
		
		final Spatial clickedNode = res.getGeometry().getParent();
		chosenCubiePos[0] = clickedNode.getUserData(Cubie.KEY_X_POS);
		chosenCubiePos[1] = clickedNode.getUserData(Cubie.KEY_Y_POS);
		chosenCubiePos[2] = clickedNode.getUserData(Cubie.KEY_Z_POS);
		
		isFaceRotating = true;
		
		return true;
	}
	
	// Reset all rotation state variables
	private void resetFaceRotationVars() {
		isFaceRotating = false;
		isFaceChosen = false;
		for(int i = 0; i < 3; ++i)
			chosenCubiePos[i] = -1;
		chosenNormVector = null;
		chosenAxis = -1;
	}
	
	// Move rotationNode cubies to cubeNode
	private void rotationNodeToCubeNode() {
		while(rotationNode.getQuantity() > 0)
			cubeNode.attachChild(rotationNode.getChild(0));
		cubeNode.detachChild(rotationNode);
	}
	
	// Move chosen cubies to rotationNode
	private void cubeNodeToRotationNode() {
		int[] lows = {0,0,0};
		int[] highs = {2,2,2};
		lows[chosenAxis] = chosenCubiePos[chosenAxis];
		highs[chosenAxis] = chosenCubiePos[chosenAxis];

		// Populate rotation node
		rotationNode.setLocalRotation(new Quaternion());
		for(int x = lows[0]; x <= highs[0]; ++x)
			for(int y = lows[1]; y <= highs[1]; ++y)
				for(int z = lows[2]; z <= highs[2]; ++z)
					rotationNode.attachChild(cube.getCubie(x, y, z).getCenterNode());

		// Add rotation node back to scene
		cubeNode.attachChild(rotationNode);
	}
	
	private Side getChosenRotationSide() {
		if(chosenAxis == 0) {
			if(chosenCubiePos[0] == 0)
				return Cubie.Side.LEFT;
			else
				return Cubie.Side.RIGHT;
		}
		else if(chosenAxis == 1) {
			if(chosenCubiePos[1] == 0)
				return Cubie.Side.BOTTOM;
			else
				return Cubie.Side.TOP;
		}
		else if(chosenAxis == 2) {
			if(chosenCubiePos[2] == 0)
				return Cubie.Side.BACK;
			else
				return Cubie.Side.FRONT;
		}
		
		return null;
	}
	
	// Returns the chosen axis of rotation
	private int getChosenRotationAxis(final Vector3f crossVector, final Vector3f upVector, final Vector3f posVector) {
		final float[] crossAbs = {Math.abs(crossVector.dot(upVector.cross(posVector))), Math.abs(crossVector.dot(upVector)), Math.abs(crossVector.dot(posVector))};
		int axis = 0;
		if(crossAbs[1] > crossAbs[0])
			axis = 1;
		if(crossAbs[2] > crossAbs[axis])
			axis = 2;
		
		return axis;
	}
	
	// Generates and performs random face rotations
	public void scrambleCube() {
		final Random random = new Random();
		final int numMoves = this.sApp.getContext().getSettings().getInteger(CCCConstants.Settings.SCRAMBLE_LENGTH);
		final Side[] sides = Side.values();
		final Direction[] dirs = Direction.values();
		
		for(int i = 0; i < numMoves; ++i) {
			final int side = random.nextInt(sides.length);
			final int dir = random.nextInt(dirs.length);
			
			cube.rotateFace(new FaceTurn(sides[side], dirs[dir]));
		}
	}
	
	public String solveCube() {
		return Search.solution(cube.toDefinitionString(), 21, 5, false);
	}
	
	public void rotateFace(FaceTurn faceTurn) {
		cube.rotateFace(faceTurn);
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

	private AbstractRotator cubeRotator;

	private float dragX = 0.0f;
	private float dragY = 0.0f;
	
	private boolean frEnabled;
	
}
