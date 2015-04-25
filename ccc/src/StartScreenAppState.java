import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
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
	
	public void switchToMode(String modeStr) {
		OverlayAppState.OverlayMode mode = OverlayAppState.OverlayMode.FREE_PLAY;
		if(modeStr.equals("timed"))
			mode = OverlayAppState.OverlayMode.TIMED_PLAY;
		else if(modeStr.equals("optimal"))
			mode = OverlayAppState.OverlayMode.OPTIMAL_MODE;
		
		NiftyAppState niftyState = sApp.getStateManager().getState(NiftyAppState.class);
		OverlayAppState overlayState = (OverlayAppState)niftyState.loadScreen(CCCConstants.Nifty.SCREEN_OVERLAY);
		overlayState.setOverlayMode(mode);
		
		sApp.getStateManager().attach(overlayState);
		sApp.getStateManager().attach(new CubeAppState());
		sApp.getStateManager().detach(this);
	}
	
	public void openSettings() {		
		NiftyAppState niftyState = sApp.getStateManager().getState(NiftyAppState.class);
		Screen screen = niftyState.getScreen(CCCConstants.Nifty.SCREEN_START);
		
		// Make the settings layer visible
		Element sLayer = screen.findElementByName(CCCConstants.Nifty.LAYER_SETTINGS);
		sLayer.setVisible(true);
		
		// Retrieve current display settings
		AppSettings settings = sApp.getContext().getSettings();
		final boolean isFullscreen = settings.isFullscreen();
		final boolean isVSync = settings.isVSync();
		final int currHeight = settings.getHeight();
		final int currWidth = settings.getWidth();
		final int currAa = settings.getSamples();
		
		// Retrieve current game settings
		final int scramLen = settings.getInteger(CCCConstants.Settings.SCRAMBLE_LENGTH);
		AbstractCubeRotator.CubeRotationType crType = (AbstractCubeRotator.CubeRotationType)settings.get(CCCConstants.Settings.CUBE_ROTATION_TYPE);
		
		// Populate fullscreen and vsync checkboxes
		screen.findNiftyControl(CCCConstants.Nifty.CHECKBOX_FULLSCREEN, CheckBox.class).setChecked(isFullscreen);
		screen.findNiftyControl(CCCConstants.Nifty.CHECKBOX_VSYNC, CheckBox.class).setChecked(isVSync);
		
		// Populate screen resolution drop down
		// TODO: Don't clear/populate this every time
		DropDown<String> srDropdown = screen.findNiftyControl(CCCConstants.Nifty.DROPDOWN_RES, DropDown.class);
		srDropdown.clear();
		displayModes = this.getDisplayModes();
		for(DisplayMode mode : displayModes) {
			srDropdown.addItem(mode.getWidth() + "x" + mode.getHeight());
			if(mode.getWidth() == currWidth && mode.getHeight() == currHeight)
				srDropdown.selectItemByIndex(srDropdown.itemCount() - 1);
		}
		
		// Populate anti-aliasing drop down
		// TODO: Don't clear/populate this every time
		DropDown<Integer> aaDropdown = screen.findNiftyControl(CCCConstants.Nifty.DROPDOWN_ANTIALIAS, DropDown.class);
		aaDropdown.clear();
		aaDropdown.addAllItems(Arrays.asList(1, 2, 4, 6, 8, 16));
		aaDropdown.selectItem(currAa);
		
		// Populate game settings elements
		screen.findNiftyControl(CCCConstants.Nifty.FIELD_SCRAMBLE, TextField.class).setText(Integer.toString(scramLen));
		if(crType == AbstractCubeRotator.CubeRotationType.FREE)
			screen.findNiftyControl(CCCConstants.Nifty.RADIO_FREE_ROTATION, RadioButton.class).select();
		else
			screen.findNiftyControl(CCCConstants.Nifty.RADIO_TT_ROTATION, RadioButton.class).select();
	}
	
	private ArrayList<DisplayMode> getDisplayModes() {
		// Get all possible display modes
		DisplayMode[] modes = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes();
		ArrayList<DisplayMode> modesList = new ArrayList<DisplayMode>(Arrays.asList(modes));
		
		// Sort the display modes by width, then height
		Collections.sort(modesList, new Comparator<DisplayMode>() {
			@Override
			public int compare(DisplayMode dm1, DisplayMode dm2) {
				if(dm1.getWidth() != dm2.getWidth())
					return dm1.getWidth() - dm2.getWidth();
				return dm1.getHeight() - dm2.getHeight();
			}
		});
		
		// Remove duplicate display modes by resolution
		for(int i = 0; i < modesList.size() - 1; ++i) {
			DisplayMode dm1 = modesList.get(i);
			DisplayMode dm2 = modesList.get(i + 1);
			if(dm1.getWidth() == dm2.getWidth() && dm1.getHeight() == dm2.getHeight())
				modesList.remove(i + 1);
		}
		
		return modesList;
	}
	
	public void applySettings() {
		NiftyAppState niftyState = sApp.getStateManager().getState(NiftyAppState.class);
		Screen screen = niftyState.getScreen(CCCConstants.Nifty.SCREEN_START);
		
		// Retrieve chosen display settings
		final boolean isFullscreen = screen.findNiftyControl(CCCConstants.Nifty.CHECKBOX_FULLSCREEN, CheckBox.class).isChecked();
		final boolean isVSync = screen.findNiftyControl(CCCConstants.Nifty.CHECKBOX_VSYNC, CheckBox.class).isChecked();
		
		final String resString = (String)screen.findNiftyControl(CCCConstants.Nifty.DROPDOWN_RES, DropDown.class).getSelection();
		final int sepIndex = resString.indexOf("x");
		final int width = Integer.parseInt(resString.substring(0, sepIndex));
		final int height = Integer.parseInt(resString.substring(sepIndex + 1));
		
		final int aa = (Integer)screen.findNiftyControl(CCCConstants.Nifty.DROPDOWN_ANTIALIAS, DropDown.class).getSelection();
		
		// Find corresponding display mode
		DisplayMode chosenMode = null;
		for(int i = 0; i < displayModes.size(); ++i) {
			chosenMode = displayModes.get(i);
			if(chosenMode.getWidth() == width && chosenMode.getHeight() == height)
				break;
		}
		if(chosenMode == null) {
			System.err.println("Failed to find corresponding display mode!");
			return;
		}
				
		final int freq = chosenMode.getRefreshRate();
		
		// Retrieve entered scramble length
		int scramLen = 100;
		try {
			scramLen = Integer.parseInt(screen.findNiftyControl(CCCConstants.Nifty.FIELD_SCRAMBLE, TextField.class).getRealText());
		} catch (NumberFormatException e) {
			// TODO: give user better feedback
			System.err.println("Scramble length is not a valid integer!");
		}
		
		// Retrieve selected rotation type
		AbstractCubeRotator.CubeRotationType crType = AbstractCubeRotator.CubeRotationType.FREE;
		if(screen.findNiftyControl(CCCConstants.Nifty.RADIO_TT_ROTATION, RadioButton.class).isActivated())
			crType = AbstractCubeRotator.CubeRotationType.TURNTABLE;
		
		AppSettings settings = sApp.getContext().getSettings();
		
		// Apply display settings
		settings.setFullscreen(isFullscreen);
		settings.setVSync(isVSync);
		settings.setResolution(width, height);
		settings.setSamples(aa);
		settings.setFrequency(freq);
		
		// Apply game settings
		settings.putInteger(CCCConstants.Settings.SCRAMBLE_LENGTH, scramLen);
		settings.put(CCCConstants.Settings.CUBE_ROTATION_TYPE, crType);
		sApp.setSettings(settings);
		sApp.restart();
	}
	
	public void closeSettings() {
		// Make the settings layer invisible
		NiftyAppState niftyState = sApp.getStateManager().getState(NiftyAppState.class);
		Screen screen = niftyState.getScreen(CCCConstants.Nifty.SCREEN_START);
		Element sLayer = screen.findElementByName(CCCConstants.Nifty.LAYER_SETTINGS);
		sLayer.setVisible(false);
	}
	
	public void quitGame() {
		sApp.stop();
	}
	
	private SimpleApplication sApp;
	private Node cubeNode;
	private ArrayList<DisplayMode> displayModes;

}
