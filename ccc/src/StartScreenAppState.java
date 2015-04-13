import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class StartScreenAppState extends AbstractAppState implements ScreenController {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		sApp = (SimpleApplication)app;
		
		// Create and setup a cube to show in middle
		Cube cube = new Cube();
		cubeNode = new Node();
		for(int x = 0; x < 3; ++x)
			for(int y = 0; y < 3; ++y)
				for(int z = 0; z < 3; ++z)
					this.cubeNode.attachChild(cube.getCubie(x, y, z).getCenterNode());
		cubeNode.scale(0.5f);
		cubeNode.rotate(FastMath.QUARTER_PI, FastMath.QUARTER_PI, 0.0f);
		sApp.getRootNode().attachChild(this.cubeNode);
	}
	
	@Override
	public void update(float tpf) {
		cubeNode.rotate(tpf * 0.2f, tpf * 0.2f, 0.0f);
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		
		sApp.getRootNode().detachChild(this.cubeNode);
		this.cubeNode.detachAllChildren();
		this.cubeNode = null;
		this.sApp = null;
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
		NiftyAppState niftyState = sApp.getStateManager().getState(NiftyAppState.class);
		AbstractAppState nextState = (AbstractAppState)niftyState.loadScreen("overlay");
		sApp.getStateManager().attach(nextState);
		sApp.getStateManager().detach(this);
	}
	
	public void quitGame() {
		sApp.stop();
	}
	
	private SimpleApplication sApp;
	private Node cubeNode;

}
