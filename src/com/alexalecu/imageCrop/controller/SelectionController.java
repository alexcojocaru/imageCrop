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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.apache.log4j.Logger;

import com.alexalecu.imageCrop.ImageCropConfig;
import com.alexalecu.imageCrop.ImageCropController;
import com.alexalecu.imageCrop.ImageCropState;
import com.alexalecu.imageCrop.event.CropSelectionEvent;
import com.alexalecu.imageCrop.event.EventBus;
import com.alexalecu.imageCrop.event.RotateSelectionEvent;
import com.alexalecu.imageCrop.event.SelectionRectangleChangedEvent;
import com.alexalecu.imageCrop.gui.ImageCropGUI;
import com.alexalecu.imageCrop.util.ImageCropUtil;
import com.alexalecu.imageUtil.ImageConvert;
import com.alexalecu.imageUtil.ImageKit;
import com.alexalecu.imageUtil.ImageRotate;
import com.google.common.eventbus.Subscribe;

/**
 * @author Alex Cojocaru
 *
 */
public class SelectionController {
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private ImageCropController controller;
	private ImageCropGUI gui;
	
	
	public SelectionController(ImageCropController controller, ImageCropGUI gui) {
		this.controller = controller;
		this.gui = gui;
		
		EventBus.register(this);
	}

	/**
	 * Get notified about changes to the selection
	 * @param event the SelectionRectangleChangedEvent containing the selection rectangle;
	 * it is null if there is no selection
	 */
	@Subscribe
	public void selectionChanged(SelectionRectangleChangedEvent event) {
		Rectangle rectangle = event.getRectangle();
		
		ImageCropConfig imageCropConfig = controller.getImageConfig();
		
		imageCropConfig.setSelectionRect(rectangle == null ?
				null : new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
		imageCropConfig.setState(rectangle != null
				? ImageCropState.StateSelectionDone
				: ImageCropState.StateSelection);
		
		gui.setState(imageCropConfig.getState());
	}

	/**
	 * crop the cropRectangle out of the current image, and set the new image as the current one;
	 * if the crop rectangle is invalid (coordinates and size are outside the current image bounds),
	 * the original image will be used as new image
	 */
	@Subscribe
	public void crop(CropSelectionEvent event) {
		Rectangle rect = controller.getImageConfig().getSelectionRect();
		BufferedImage image = controller.getImage();
		
		if (rect == null) {
			gui.showErrorDialog("No selection found !");
			return;
		}
		
		logger.debug("Crop image (x, y, w, h): " + rect.x + ", " + rect.y +
				", " + rect.width + ", " + rect.height);
		
		if (rect.x < 0 || rect.y < 0 || rect.width <= 0 || rect.height <= 0 ||
				rect.x + rect.width >= image.getWidth() ||
				rect.y + rect.height >= image.getHeight())
			pushImage(image);
		else
			pushImage(ImageConvert.cropImage(image, rect));
	}

	/**
	 * rotate the current image image in buffer; if there is a selection, it will be lost - the user
	 * is asked to confirm that
	 */
	@Subscribe
	public void rotate(RotateSelectionEvent event) {
		final String lineSeparator = System.getProperty("line.separator");
		
		// exit is there is a select and the user does not want to discard it
		if (controller.getImageConfig().getSelectionRect() != null) {
			if (!gui.showConfirmDialog("Rotating image will lost the current selection." +
					lineSeparator + "Do you want to continue ?"))
				return;
		}

		// get the rotation amount from the user
		String degStr = gui.showInputDialog("Degrees to rotate the image anticlockwise (ex: 5.7): ");
		if (degStr == null || degStr.trim().length() == 0)
			return;
		double deg;
		try {
			deg = Double.parseDouble(degStr);
		}
		catch (NumberFormatException ex) {
			gui.showErrorDialog("Invalid numeric value !");
			return;
		}

		logger.debug("Rotate image; deg = " + deg);

		// rotate the initial image, the result will be a new image
		BufferedImage image = null;
		try {
			image = ImageRotate.rotateDegrees(controller.getImage(), deg, 
					ImageRotate.ROTATE_BOUNDING_BOX_OPTIMAL, null);
		}
		catch (Throwable tr) {
			logger.debug("", tr);
			
			// we might not have enough memory; tell the user to crop first to reduce the image size
			String msg = "An error has occured while rotating image !";
			if (tr instanceof OutOfMemoryError)
				msg += lineSeparator + "The image is too big. Try croping it first.";
			gui.showErrorDialog(msg);
			
			return;
		}

		// compute the maximum hull which fits inside the rotated image
		Object res[] = ImageKit.autoSelectBoundingRectangle(image,
				new Rectangle(0, 0, image.getWidth(), image.getHeight()), Color.BLACK, 0, -1);
		
		Rectangle cropRect = (Rectangle)res[0];

		logger.debug("Auto select result (x, y, w, h): " + cropRect.x + ", " + cropRect.y + ", "
				+ cropRect.width + ", " + cropRect.height);

		// make sure that the resulting rectangle is valid and fits inside the current image
		if (!ImageCropUtil.validateSelectionRectangle(image, cropRect)) {
			gui.showErrorDialog("An error has occured. Try again !");
			logger.debug("An error has occured; rotated image size: " +
					image.getWidth() + "x" + image.getHeight());
			return;
		}

		// crop the image according to the resulting rectangle and make it the current image in buffer
		pushImage(ImageConvert.cropImage(image, cropRect));
	}


	/**
	 * make this image the current one in buffer; if the previous image is the initial one, add
	 * this image to the stack, otherwise replace the last image in stack with this one
	 * @param image the new image to use
	 */
	private void pushImage(BufferedImage image) {
		logger.debug("imageConfigStack.size() = " + controller.getImageConfigStackSize());

		// we only have the initial image in stack; add the new one
		if (controller.getImageConfigStackSize() == 1) {
			ImageCropConfig imageCropConfig;
			try {
				imageCropConfig = (ImageCropConfig)controller.getImageConfig().clone();
			}
			catch (CloneNotSupportedException e) {
				logger.error("Cannot create new image config object", e);
				gui.showErrorDialog("Cannot create a new image!");
				return;
			}
			controller.pushImageConfig(imageCropConfig);

			logger.debug("Adding image in images stack");
		}
		else { // replace the last element with the current one
			logger.debug("Replacing last image in images stack");
		}

		ImageCropConfig imageCropConfig = controller.getImageConfig();
		
		// set the image config
		imageCropConfig.setScaleFactor(1d);
		imageCropConfig.setState(ImageCropState.StateImageLoaded);
		imageCropConfig.setSelectionRect(null);

		controller.setImage(image);

		controller.setScaleFactorToFit();

		controller.logCurrentConfig();

		// update the GUI to match the current state
		gui.setSelectionRect(imageCropConfig.getSelectionRect(), true);
		gui.setState(imageCropConfig.getState());
		gui.setScaleFactor(image, imageCropConfig.getScaleFactor());
		gui.setImageSize(new Dimension(image.getWidth(), image.getHeight()));
	}
	
}
