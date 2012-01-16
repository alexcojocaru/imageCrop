/*
 * Copyright (C) 2012 Alex Cojocaru
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.alexalecu.imageCrop.controller;

import org.apache.log4j.Logger;

import com.alexalecu.event.AutoSelectMethodChangedEvent;
import com.alexalecu.event.BgColorPickedEvent;
import com.alexalecu.event.BgColorSelectedEvent;
import com.alexalecu.event.BgToleranceChangedEvent;
import com.alexalecu.event.EventBus;
import com.alexalecu.event.ScaleFactorChangedEvent;
import com.alexalecu.event.ToggleBgSelectionEvent;
import com.alexalecu.imageCrop.ImageCropConfig;
import com.alexalecu.imageCrop.ImageCropController;
import com.alexalecu.imageCrop.ImageCropState;
import com.alexalecu.imageCrop.gui.ImageCropGUI;
import com.google.common.eventbus.Subscribe;

/**
 * @author Alex Cojocaru
 *
 */
public class ImageConfigController {
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private ImageCropController controller;
	private ImageCropGUI gui;

	
	public ImageConfigController(ImageCropController controller, ImageCropGUI gui) {
		this.controller = controller;
		this.gui = gui;
		
		EventBus.register(this);
	}

	/**
	 * Get notified when a new background color has been selected
	 * @param event BgColorSelectedEvent containing the new background color
	 */
	@Subscribe
	public void bgColorChanged(BgColorSelectedEvent event) {
		controller.getImageConfig().setBgColor(event.getColor());
	}

	/**
	 * Get notified when a new bg color has been picked
	 * @param event the BgColorPickedEvent containing the new background color
	 */
	public void bgColorPicked(BgColorPickedEvent event) {
		controller.getImageConfig().setBgColor(event.getColor());
		toggleSelectBackgroundMode(null); // exit the bg selection mode after using the color picker
	}
	
	/**
	 * Get notified about changes to the background tolerance
	 * @param event the BgToleranceChangedEvent containing the new background color tolerance
	 */
	@Subscribe
	public void bgToleranceChanged(BgToleranceChangedEvent event) {
		controller.getImageConfig().setBgTolerance(event.getTolerance());
	}
	
	/**
	 * Get notified about changes to the auto select method
	 * @param event the AutoSelectMethodChangedEvent containing the new select method
	 */
	@Subscribe
	public void autoSelectMethodChanged(AutoSelectMethodChangedEvent event) {
		controller.getImageConfig().setSelectMethod(event.getImageSelectMethod());
	}

	/**
	 * apply the scale factor to the image in buffer
	 * @param event the ScaleFactorChangedEvent containing the scale factor to apply
	 * @return true if the image has been scaled
	 */
	@Subscribe
	public boolean scaleFactorChanged(ScaleFactorChangedEvent event) {
		double scaleFactor = (event.getScale()) / 100d;
		
		ImageCropConfig imageCropConfig = controller.getImageConfig();
		
		// skip scaling if the is no change in the scale factor
		if (scaleFactor == imageCropConfig.getScaleFactor())
			return false;
		
		if (controller.getImage() == null)
			return false;

		logger.debug("Scale to: " + scaleFactor);

		imageCropConfig.setScaleFactor(scaleFactor);

		// tell the GUI to update the displayed image to reflect the new scale factor
		gui.setScaleFactor(controller.getImage(), scaleFactor);
		gui.setSelectionRect(imageCropConfig.getSelectionRect(), true);
		
		return true;
	}

	
	/**
	 * enter / exit the select background color mode
	 */
	@Subscribe
	public void toggleSelectBackgroundMode(ToggleBgSelectionEvent event) {
		ImageCropState state = controller.getImageConfig().getState();
		boolean isSelectBgMode = state == ImageCropState.StateSelectingBackgroundColor;

		// toggle the state
		isSelectBgMode = !isSelectBgMode;

		if (isSelectBgMode)
			state = ImageCropState.StateSelectingBackgroundColor;
		else if (controller.getImageConfig().getSelectionRect() != null)
			state = ImageCropState.StateSelectionDone;
		else
			state = ImageCropState.StateBackgroundColor;
		
		// the controller will call back to let me know what's the new state
		controller.setState(state);
	}
}
