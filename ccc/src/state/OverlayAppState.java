package state;
import util.CCCConstants;
import util.StopWatch;

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
		FREE_PLAY, TIMED_PLAY, OPTIMAL_MODE
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		sApp = (SimpleApplication)app;
		
		NiftyAppState niftyState = stateManager.getState(NiftyAppState.class);
		Screen screen = niftyState.getScreen(CCCConstants.Nifty.SCREEN_OVERLAY);
		textTime = screen.findElementByName(CCCConstants.Nifty.TEXT_TIME);
		textSolution = screen.findElementByName(CCCConstants.Nifty.TEXT_SOLUTION);
		buttonSolve = screen.findElementByName(CCCConstants.Nifty.BUTTON_SOLVE);
		if(this.currMode == OverlayMode.FREE_PLAY) {
			textTime.setVisible(false);
			textSolution.setVisible(false);
			buttonSolve.setVisible(false);
		}
		else if(this.currMode == OverlayMode.TIMED_PLAY) {
			textTime.setVisible(true);
			textSolution.setVisible(false);
			buttonSolve.setVisible(false);
			
			updateTextTime(0);
		}
		else if(this.currMode == OverlayMode.OPTIMAL_MODE) {
			textTime.setVisible(false);
			textSolution.setVisible(true);
			buttonSolve.setVisible(true);
			
			updateTextSolution("");
		}
		
		cubeState = sApp.getStateManager().getState(CubeAppState.class);
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
			if(cubeState.isCubeSolved())
				stopWatch.stop();
			
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
		cubeState.scrambleCube();
		
		// If we're in timed mode, restart the timer
		if(currMode == OverlayMode.TIMED_PLAY)
			stopWatch.start();
		else if(currMode == OverlayMode.OPTIMAL_MODE)
			updateTextSolution("");
	}
	
	public void solveCube() {
		updateTextSolution("solving...");
		updateTextSolution(cubeState.solveCube());
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
	
	private void updateTextSolution(String sol) {
		textSolution.getRenderer(TextRenderer.class).setText("Solution: " + sol);
	}
	
	private SimpleApplication sApp;
	private OverlayMode currMode = OverlayMode.FREE_PLAY;
	private StopWatch stopWatch = new StopWatch();
	
	private CubeAppState cubeState;
	private Element textTime;
	private Element textSolution;
	private Element buttonSolve;

}