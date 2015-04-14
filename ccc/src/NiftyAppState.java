import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class NiftyAppState extends AbstractAppState {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		sApp = (SimpleApplication)app;
				
		niftyDisplay = new NiftyJmeDisplay(sApp.getAssetManager(), sApp.getInputManager(), sApp.getAudioRenderer(), sApp.getGuiViewPort());
		final String xmlFilename = "Interface/screen.xml";
		try {
			niftyDisplay.getNifty().validateXml(xmlFilename);
		} catch (Exception e) {
			System.err.println("XML failed validation!");
			e.printStackTrace();
		}
		niftyDisplay.getNifty().addXml(xmlFilename);
		sApp.getGuiViewPort().addProcessor(niftyDisplay);
		
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
		initialScreen = null;
	}
	
	public ScreenController loadScreen(String screen) {
		niftyDisplay.getNifty().gotoScreen(screen);
		return niftyDisplay.getNifty().getScreen(screen).getScreenController();
	}
	
	public void setInitialScreen(String initialScreen) {
		this.initialScreen = initialScreen;
	}
	
	public Screen getScreen(String screen) {
		return niftyDisplay.getNifty().getScreen(screen);
	}
	
	private NiftyJmeDisplay niftyDisplay;
	private SimpleApplication sApp;
	private String initialScreen;

}
