import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;

public class Main extends SimpleApplication {
	
	public static void main(String[] args) {
		Main app = new Main();
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
		Cube cube = new Cube();
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					this.rootNode.attachChild(cube.getCubie(x, y, z).getCenterNode());
	}
	
	private void initInput() {
		this.inputManager.addMapping("Rotate_Trigger", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		this.inputManager.addMapping("Rotate_Left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		this.inputManager.addMapping("Rotate_Right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		this.inputManager.addMapping("Rotate_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		this.inputManager.addMapping("Rotate_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		
		this.inputManager.addListener(clickListener, "Rotate_Trigger");
		this.inputManager.addListener(moveListener, "Rotate_Left", "Rotate_Right", "Rotate_Up", "Rotate_Down");
	}
	
	private ActionListener clickListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if(name.equals("Rotate_Trigger"))
				isRotating = keyPressed;
		}
	};
	
	private AnalogListener moveListener = new AnalogListener() {
		public void onAnalog(String name, float value, float tpf) {
			if(!isRotating)
				return;
			
			if(name.equals("Rotate_Left"))
				rootNode.rotate(0.0f, value * 4, 0.0f);
			else if(name.equals("Rotate_Right"))
				rootNode.rotate(0.0f, -value * 4, 0.0f);
			else if(name.equals("Rotate_Up"))
				rootNode.rotate(value * 4, 0.0f, 0.0f);
			else if(name.equals("Rotate_Down"))
				rootNode.rotate(-value * 4, 0.0f, 0.0f);
		}
	};
	
	private boolean isRotating = false;

}
