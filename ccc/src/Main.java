
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

public class Main extends SimpleApplication {
	
	public static void main(String[] args) {
		// Setup default settings
		AppSettings settings = new AppSettings(true);
		settings.setTitle("CCC");
		settings.setResolution(800, 600);
		settings.setSamples(16);
		settings.setVSync(true);
		
		// Create application and start it
		Main app = new Main();
		app.setSettings(settings);
		app.setShowSettings(true);
		app.stateManager.attach(new CubeAppState());
		app.start();
	}

	@Override
	public void simpleInitApp() {
	}
	
	@Override
	public void simpleUpdate(float tpf) {
	}
	
}
