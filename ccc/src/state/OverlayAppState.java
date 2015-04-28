package state;

import java.util.ArrayList;

import org.kociemba.twophase.Search;

import util.CCCConstants;
import util.StopWatch;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import cube.FaceTurn;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;

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
		Element buttonSolve = screen.findElementByName(CCCConstants.Nifty.BUTTON_SOLVE);
		Element buttonNext = screen.findElementByName(CCCConstants.Nifty.BUTTON_NEXT);
		Element buttonPrev = screen.findElementByName(CCCConstants.Nifty.BUTTON_PREV);
		if(this.currMode == OverlayMode.FREE_PLAY) {
			textTime.setVisible(false);
			textSolution.setVisible(false);
			buttonSolve.setVisible(false);
			buttonNext.setVisible(false);
			buttonPrev.setVisible(false);
		}
		else if(this.currMode == OverlayMode.TIMED_PLAY) {
			textTime.setVisible(true);
			textSolution.setVisible(false);
			buttonSolve.setVisible(false);
			buttonNext.setVisible(false);
			buttonPrev.setVisible(false);
			
			updateTextTime(0);
		}
		else if(this.currMode == OverlayMode.OPTIMAL_MODE) {
			textTime.setVisible(false);
			textSolution.setVisible(true);
			buttonSolve.setVisible(true);
			buttonNext.setVisible(true);
			buttonPrev.setVisible(true);
			
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
		else if(currMode == OverlayMode.OPTIMAL_MODE) {
			cubeState.enableFaceRotation(true);
			updateTextSolution("");
		}
	}
	
	public void solveCube() {
		cubeState.enableFaceRotation(false);
		updateTextSolution("solving...");
		
		final String solStr = cubeState.solveCube();
		updateTextSolution(solStr);
		
		faceTurns = FaceTurn.getFaceTurns(solStr);
		turnNum = -1;
	}
	
	public void scanCube() {
		System.out.println("Scan cube");
		final camera.WebcamPanelFrame frame = new camera.WebcamPanelFrame();
		frame.setLocationRelativeTo(null);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// TODO: Make this threaded instead of sleeping!
		while(frame.isVisible()) {
			try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		final String scannedDefStr = frame.getResult();
		System.out.println("scanCube() produced def string: " + scannedDefStr);
		
		// Solve the cube from the scanned state
		final String solScanned = Search.solution(scannedDefStr, 21, 5, false);
		if(solScanned.startsWith("Error")) {
			System.err.println("Scanned cube is invalid!");
			return;
		}
		final ArrayList<FaceTurn> turnsToSolveScanned = FaceTurn.getFaceTurns(solScanned);
		
		// Solve the cube from its current state
		final String solCurrent = this.cubeState.solveCube();
		final ArrayList<FaceTurn> turnsToSolveCurrent = FaceTurn.getFaceTurns(solCurrent);
		
		// Turns to create scanned state
		final ArrayList<FaceTurn> turnsToCreateScanned = new ArrayList<FaceTurn>(turnsToSolveCurrent);
		for(int i = turnsToSolveScanned.size() - 1; i >= 0; --i)
			turnsToCreateScanned.add(FaceTurn.reversed(turnsToSolveScanned.get(i)));
		
		// Print the turns to create scanned state
		System.out.println(turnsToCreateScanned.toString());
		
		// Perform the turns to create scanned state
		for(int i = 0; i < turnsToCreateScanned.size(); ++i)
			cubeState.rotateFace(turnsToCreateScanned.get(i));
	}
	
	public void nextTurn() {
		if(turnNum >= faceTurns.size() - 1)
			return;
		
		++turnNum;
		
		final FaceTurn nextTurn = faceTurns.get(turnNum);
		highlightIndex += nextTurn.toString().length() + 1;
		updateTextSolutionHighlight();
		
		cubeState.rotateFace(nextTurn);
	}
	
	public void prevTurn() {
		if(turnNum < 0)
			return;
		
		final FaceTurn prevTurn = faceTurns.get(turnNum);
		highlightIndex -= prevTurn.toString().length() + 1;
		updateTextSolutionHighlight();
		
		cubeState.rotateFace(FaceTurn.reversed(prevTurn));
		
		--turnNum;
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
	
	private void updateTextSolutionHighlight() {
		TextRenderer rend = textSolution.getRenderer(TextRenderer.class);
		rend.setSelection(0, rend.getOriginalText().length() - 1);
		rend.setTextSelectionColor(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		rend.setSelection(9, highlightIndex);
		rend.setTextSelectionColor(new Color(0.65f, 0.65f, 0.65f, 1.0f));
	}
	
	private SimpleApplication sApp;
	private OverlayMode currMode = OverlayMode.FREE_PLAY;
	private StopWatch stopWatch = new StopWatch();
	
	private CubeAppState cubeState;
	private Element textTime;
	private Element textSolution;
	
	private ArrayList<FaceTurn> faceTurns;
	private int turnNum;
	private int highlightIndex = 9;

}