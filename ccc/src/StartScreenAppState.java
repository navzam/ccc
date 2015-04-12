import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;

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
	}
	
	@Override
	public void update(float tpf) {
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
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

}
