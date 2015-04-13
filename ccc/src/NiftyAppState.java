import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.screen.ScreenController;

public class NiftyAppState extends AbstractAppState {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		sApp = (SimpleApplication)app;
		
		sApp.getFlyByCamera().setEnabled(false);
		niftyDisplay = new NiftyJmeDisplay(sApp.getAssetManager(), sApp.getInputManager(), sApp.getAudioRenderer(), sApp.getGuiViewPort());
		niftyDisplay.getNifty().addXml("Interface/screen.xml");
		app.getGuiViewPort().addProcessor(niftyDisplay);
		
		// Load initial state and attach corresponding state
		AbstractAppState initialState = (AbstractAppState)loadScreen(this.initialScreen);
		stateManager.attach(initialState);
	}
	
	@Override
	public void update(float tpf) {
	}
	
	@Override
	public void cleanup() {
		sApp.getGuiViewPort().removeProcessor(niftyDisplay);
		
		niftyDisplay = null;
		sApp = null;
	}
	
	public ScreenController loadScreen(String screen) {
		niftyDisplay.getNifty().gotoScreen(screen);
		return niftyDisplay.getNifty().getScreen(screen).getScreenController();
	}
	
	public void setInitialScreen(String initialScreen) {
		this.initialScreen = initialScreen;
	}
	
	private NiftyJmeDisplay niftyDisplay;
	private SimpleApplication sApp;
	private String initialScreen;

}
