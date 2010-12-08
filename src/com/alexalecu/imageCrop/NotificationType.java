package com.alexalecu.imageCrop;

/**
 * Class to hold the notification types passed around in the application.
 * Naming convention:
 *   *_PICKED, *_SELECTED, *_CHANGED: the property has been changed by the user;
 *   *_UPDATED: the property has changed programmatically and the GUI has to be changed to reflect
 *   the new value
 */
public class NotificationType {

	public final static String BG_COLOR_PICKED = "bgColorPicked";
	public final static String BG_COLOR_SELECTED = "bgColorSelected";
	public final static String BG_COLOR_UPDATED = "bgColorChanged";
	
	public final static String BG_TOLERANCE_CHANGED = "bgToleranceChanged";
	
	public final static String SELECTION_RECTANGLE_CHANGED = "selectionRectangleChanged";
	
	public final static String SCALE_FACTOR_CHANGED = "scaleFactorChanged";
	
	public final static String TOGGLE_BG_SELECTION = "toggleBgSelection";
	
	public final static String CROP_SELECTION_ACTION = "cropSelectionAction";
	public final static String ROTATE_SELECTION_ACTION = "rotateSelectionAction";
	public final static String DISCARD_IMAGE_ACTION = "discardImageAction";
	public final static String SAVE_IMAGE_AS_ACTION = "saveImageAsAction";
	public final static String SAVE_IMAGE_ACTION = "saveImageAction";
	public final static String LOAD_IMAGE_ACTION = "loadImageAction";
	public final static String TOGGLE_WIZARD_ACTION = "togleWizardAction";
	
	public final static String AUTO_SELECT_RECTANGLE = "autoSelectRectangle";
	public final static String AUTO_SELECT_METHOD_SELECTED = "autoSelectMethodSelected";
	
	public final static String MOVE_SELECTION = "moveSelection";
	public final static String RESIZE_SELECTION = "resizeSelection";

	public final static String EXIT_APP = "exitApp";
}
