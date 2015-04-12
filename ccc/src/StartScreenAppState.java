import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;


public class StartScreenAppState extends AbstractAppState implements ScreenController {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = (SimpleApplication)app;
		this.app.getFlyByCamera().setEnabled(false);
		
		niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(), this.app.getInputManager(), this.app.getAudioRenderer(), this.app.getGuiViewPort());
		niftyDisplay.getNifty().fromXml("Interface/screen.xml", "start", this);
		this.app.getGuiViewPort().addProcessor(niftyDisplay);
		
		// Create and setup a cube to show in middle
		Cube cube = new Cube();
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					this.cubeNode.attachChild(cube.getCubie(x, y, z).getCenterNode());
		cubeNode.scale(0.5f);
		cubeNode.rotate(FastMath.QUARTER_PI, FastMath.QUARTER_PI, 0.0f);
		this.app.getRootNode().attachChild(this.cubeNode);
	}
	
	@Override
	public void update(float tpf) {
		cubeNode.rotate(tpf * 0.2f, tpf * 0.2f, 0.0f);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		this.app.getRootNode().detachChild(this.cubeNode);
		app.getGuiViewPort().removeProcessor(niftyDisplay);
	}

	@Override
	public void bind(Nifty nifty, Screen screen) {
	}

	@Override
	public void onEndScreen() {
	}

	@Override
	public void onStartScreen() {
	}
	
	public void switchToFreePlay() {
		app.getStateManager().attach(new CubeAppState());
		app.getStateManager().detach(this);
	}
	
	public void quitGame() {
		app.stop();
	}
	
	private SimpleApplication app;
	private NiftyJmeDisplay niftyDisplay;
	private Node cubeNode = new Node();

}
