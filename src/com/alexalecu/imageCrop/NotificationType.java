package com.alexalecu.imageCrop;

/**
 * Class to hold the notification types passed around in the application.
 * Naming convention:
 *   *_SELECTED: the property has been changed by the user;
 *   *_CHANGED: the property has changed programmatically and the GUI has to be changed to reflect
 *   the new value
 */
public class NotificationType {

	public final static String BG_COLOR_PICKED = "bgColorPicked";
	public final static String BG_COLOR_SELECTED = "bgColorSelected";
	public final static String BG_COLOR_CHANGED = "bgColorChanged";
	
	public final static String BG_TOLERANCE_CHANGED = "bgToleranceChanged";
	public final static String TOGGLE_BG_SELECTION = "toggleBgSelection";
	
}
