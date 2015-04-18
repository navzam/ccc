
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
	
	public static void main(String[] args) {
		// Setup default settings
		AppSettings settings = new AppSettings(true);
		settings.setTitle("CCC");
		settings.setResolution(1024, 768);
		settings.setSamples(16);
		settings.setVSync(true);
		
		// Create application and start it
		Main app = new Main();
		app.setSettings(settings);
		app.setShowSettings(true);
		app.start();
	}

	@Override
	public void simpleInitApp() {
		setupDefaultMats();
		
		//Disable the default fly camera
		flyCam.setEnabled(false);
		
		// Attach the nifty state
		NiftyAppState niftyState = new NiftyAppState();
		niftyState.setInitialScreen("start");
		this.stateManager.attach(niftyState);
	}
	
	@Override
	public void simpleUpdate(float tpf) {
	}
	
	private void setupDefaultMats() {
		// Create default materials
		Material blueMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material greenMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material redMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material orangeMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material yellowMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material whiteMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		Material blackMat = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		// Set colors of default materials
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
	}
		
}
