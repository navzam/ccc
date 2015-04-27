package util;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

public final class CCCConstants {
	
	public final static class Settings {
		public final static String SCRAMBLE_LENGTH = "scramble_length";
		public final static String CUBE_ROTATION_TYPE = "cr_type";
	}
	
	public final static class Nifty {
		public final static String SCREEN_START = "screen_start";
		public final static String SCREEN_OVERLAY = "screen_overlay";
		public final static String TEXT_TIME = "text_time";
		public final static String TEXT_SOLUTION = "text_solution";
		public final static String BUTTON_SOLVE = "button_solve";
		public final static String BUTTON_NEXT = "button_next";
		public final static String BUTTON_PREV = "button_prev";
		public final static String LAYER_SETTINGS = "layer_settings";
		public final static String CHECKBOX_FULLSCREEN = "fs_checkbox";
		public final static String CHECKBOX_VSYNC = "vs_checkbox";
		public final static String DROPDOWN_RES = "sr_dropdown";
		public final static String DROPDOWN_ANTIALIAS = "aa_dropdown";
		public final static String FIELD_SCRAMBLE = "scr_field";
		public final static String RADIO_FREE_ROTATION = "opt_fr";
		public final static String RADIO_TT_ROTATION = "opt_tt";
	}
	
	public final static class Input {
		public final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
		public final static Trigger TRIGGER_ROTATE_LEFT = new MouseAxisTrigger(MouseInput.AXIS_X, false);
		public final static Trigger TRIGGER_ROTATE_RIGHT = new MouseAxisTrigger(MouseInput.AXIS_X, true);
		public final static Trigger TRIGGER_ROTATE_UP = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
		public final static Trigger TRIGGER_ROTATE_DOWN = new MouseAxisTrigger(MouseInput.AXIS_Y, false);
		
		public final static Trigger TRIGGER_DIRECT_LEFT = new KeyTrigger(KeyInput.KEY_LEFT);
		public final static Trigger TRIGGER_DIRECT_RIGHT = new KeyTrigger(KeyInput.KEY_RIGHT);
		public final static Trigger TRIGGER_DIRECT_UP = new KeyTrigger(KeyInput.KEY_UP);
		public final static Trigger TRIGGER_DIRECT_DOWN = new KeyTrigger(KeyInput.KEY_DOWN);

		public final static String MAPPING_ROTATE = "Rotate Start";
		public final static String MAPPING_ROTATE_LEFT = "Rotate Left";
		public final static String MAPPING_ROTATE_RIGHT = "Rotate Right";
		public final static String MAPPING_ROTATE_UP = "Rotate Up";
		public final static String MAPPING_ROTATE_DOWN = "Rotate Down";
		
		public final static String MAPPING_DIRECT_LEFT = "Direct Left";
		public final static String MAPPING_DIRECT_RIGHT = "Direct Right";
		public final static String MAPPING_DIRECT_UP = "Direct Up";
		public final static String MAPPING_DIRECT_DOWN = "Direct Down";
	}
	
	private CCCConstants() {
		
	}

}
