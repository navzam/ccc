import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class OverlayAppState extends AbstractAppState implements ScreenController {
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
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
		System.out.println("Scramble cube");
	}

}
