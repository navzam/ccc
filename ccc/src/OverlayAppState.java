import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class OverlayAppState extends AbstractAppState implements ScreenController {
	
	public static enum OverlayMode {
		FREE_PLAY, TIMED_PLAY
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		sApp = (SimpleApplication)app;
		
		NiftyAppState niftyState = stateManager.getState(NiftyAppState.class);
		Screen screen = niftyState.getScreen("overlay");
		textTime = screen.findElementByName("text_time");
		if(this.currMode == OverlayMode.FREE_PLAY)
			textTime.setVisible(false);
		else {
			textTime.setVisible(true);
			updateTextTime(0);
		}
	}

	@Override
	public void cleanup() {
		super.cleanup();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	@Override
	public void update(float tpf) {
		if(currMode == OverlayMode.TIMED_PLAY && stopWatch.isRunning()) {
			this.updateTextTime(stopWatch.getElapsedMillis());
		}
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
	
	public void scrambleCube() {
		CubeAppState cubeState = sApp.getStateManager().getState(CubeAppState.class);
		if(cubeState == null) {
			System.err.println("Scramble failed because cube state is not attached!");
			return;
		}
		
		cubeState.scrambleCube();
		
		// If we're in timed mode, restart the timer
		if(currMode == OverlayMode.TIMED_PLAY)
			stopWatch.start();
		
	}
	
	public void setOverlayMode(OverlayMode mode) {
		currMode = mode;
	}
	
	private void updateTextTime(long totalMillis) {
		final long second = (totalMillis / 1000) % 60;
		final long minute = (totalMillis / (1000 * 60)) % 60;
		final long millis = totalMillis % 1000;

		final String time = String.format("%02d:%02d:%03d", minute, second, millis);
		textTime.getRenderer(TextRenderer.class).setText("Time: " + time);
	}
	
	private SimpleApplication sApp;
	private OverlayMode currMode = OverlayMode.FREE_PLAY;
	private StopWatch stopWatch = new StopWatch();
	private Element textTime;

}