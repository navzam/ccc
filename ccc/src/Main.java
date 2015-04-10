
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
	
	public static void main(String[] args) {
		// Setup default settings
		AppSettings settings = new AppSettings(true);
		settings.setTitle("CCC");
		settings.setResolution(800, 600);
		settings.setSamples(16);
		settings.setVSync(true);
		
		Main app = new Main();
		app.setSettings(settings);
		app.setShowSettings(true);
		app.start();
	}

	@Override
	public void simpleInitApp() {
		
		// Disable the default controls
		this.flyCam.setEnabled(false);
		initInput();
				
		// Create default materials
		Material blueMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material greenMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material redMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material orangeMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material yellowMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material whiteMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material blackMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		// Setup default materials
		blueMat.setColor("Color", new Color(Color.ColorName.BLUE).getRGBA());
		greenMat.setColor("Color", new Color(Color.ColorName.GREEN).getRGBA());
		redMat.setColor("Color", new Color(Color.ColorName.RED).getRGBA());
		orangeMat.setColor("Color", new Color(Color.ColorName.ORANGE).getRGBA());
		yellowMat.setColor("Color", new Color(Color.ColorName.YELLOW).getRGBA());
		whiteMat.setColor("Color", new Color(Color.ColorName.WHITE).getRGBA());
		blackMat.setColor("Color", new Color(Color.ColorName.DEFAULT).getRGBA());
		
		// Setup MaterialManager with default materials
		MaterialManager.setMaterial(Cubie.Side.FRONT, redMat);
		MaterialManager.setMaterial(Cubie.Side.BACK, orangeMat);
		MaterialManager.setMaterial(Cubie.Side.LEFT, blueMat);
		MaterialManager.setMaterial(Cubie.Side.RIGHT, greenMat);
		MaterialManager.setMaterial(Cubie.Side.TOP, yellowMat);
		MaterialManager.setMaterial(Cubie.Side.BOTTOM, whiteMat);
		MaterialManager.setMaterial(null, blackMat);
		
		// Create cube and add cubies to scene
		cube = new Cube();
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					this.rootNode.attachChild(cube.getCubie(x, y, z).getCenterNode());
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		// If the cube is currently rotating
		if(isCubeRotating) {
			// Apply rotation to direction vectors
			Quaternion addRot = new Quaternion(moveY * 4, -moveX * 4, 0.0f, 1.0f);
			addRot.multLocal(posVector);
			addRot.multLocal(upVector);
			
			// Rotate cube
			rootNode.lookAt(posVector, upVector);
			
			// Reset movement variables
			moveX = 0.0f;
			moveY = 0.0f;
		}
		// If a face is currently rotating
		else if(isFaceRotating) {
			// If a face has not yet been chosen
			if(!isFaceChosen) {
				// If we are ready to choose a face
				if(Math.abs(moveX) > 0.01f || Math.abs(moveY) > 0.01f) {
					isFaceChosen = true;
					
					// Determine which face to rotate
					Vector3f cross = new Vector3f();
					chosenNormVector.cross(moveX, moveY, 0.0f, cross);
					
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
					rootNode.attachChild(rotationNode);
				}
			}
			// A face has been chosen
			else {
				Vector3f cross = new Vector3f();
				chosenNormVector.cross(moveX, moveY, 0.0f, cross);
				
				final float speedMultiplier = 6.0f;
				if(chosenAxis == 0)
					rotationNode.rotate(-cross.dot(upVector.cross(posVector)) * speedMultiplier, 0.0f, 0.0f);
				else if(chosenAxis == 1)
					rotationNode.rotate(0.0f, -cross.dot(upVector) * speedMultiplier, 0.0f);
				else if(chosenAxis == 2)
					rotationNode.rotate(0.0f, 0.0f, -cross.dot(posVector) * speedMultiplier);	
				
				moveX = 0.0f;
				moveY = 0.0f;
			}
		}
	}
	
	private void initInput() {
		this.inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
		this.inputManager.addMapping(MAPPING_ROTATE_LEFT, TRIGGER_ROTATE_LEFT);
		this.inputManager.addMapping(MAPPING_ROTATE_RIGHT, TRIGGER_ROTATE_RIGHT);
		this.inputManager.addMapping(MAPPING_ROTATE_UP, TRIGGER_ROTATE_UP);
		this.inputManager.addMapping(MAPPING_ROTATE_DOWN, TRIGGER_ROTATE_DOWN);
		
		this.inputManager.addListener(clickListener, MAPPING_ROTATE);
		this.inputManager.addListener(moveListener, MAPPING_ROTATE_LEFT, MAPPING_ROTATE_RIGHT, MAPPING_ROTATE_UP, MAPPING_ROTATE_DOWN);
	}
	
	private ActionListener clickListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if(name.equals(MAPPING_ROTATE)) {
				// If rotate trigger was pressed
				if(keyPressed) {
					// Calculate collisions with cubies
					Vector2f click2d = inputManager.getCursorPosition();
					Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0.0f).clone();
					Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1.0f).subtractLocal(click3d).normalizeLocal();
					
					Ray ray = new Ray(click3d, dir);
					CollisionResults results = new CollisionResults();
					rootNode.collideWith(ray, results);
					
					// If a cubie was clicked
					if(results.size() != 0) {
						CollisionResult res = results.getClosestCollision();
						Vector3f contactNormal = res.getContactNormal();
						
						chosenCubiePos[0] = res.getGeometry().getParent().getUserData(Cubie.KEY_X_POS);
						chosenCubiePos[1] = res.getGeometry().getParent().getUserData(Cubie.KEY_Y_POS); 
						chosenCubiePos[2] = res.getGeometry().getParent().getUserData(Cubie.KEY_Z_POS);
						
						chosenNormVector = contactNormal;
					}
					
					if(results.size() == 0)
						isCubeRotating = true;
					else
						isFaceRotating = true;
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
							rootNode.attachChild(rotationNode.getChild(0));
						rootNode.detachChild(rotationNode);
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
				moveX -= value;
			else if(name.equals(MAPPING_ROTATE_RIGHT))
				moveX += value;
			else if(name.equals(MAPPING_ROTATE_UP))
				moveY += value;
			else if(name.equals(MAPPING_ROTATE_DOWN))
				moveY -= value;
		}
	};
	
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
	
	private float moveX = 0.0f;
	private float moveY = 0.0f;
}
